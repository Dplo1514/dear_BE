package com.sparta.hh99_actualproject.service;

import com.sparta.hh99_actualproject.dto.ChatRoomDto;
import com.sparta.hh99_actualproject.dto.ChatRoomDto.ChatRoomResRequestDto;
import com.sparta.hh99_actualproject.dto.ChatRoomDto.ChatRoomResUpdateDto;
import com.sparta.hh99_actualproject.dto.ChatRoomDto.ChatRoomResponseDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.ChatRoom;
import com.sparta.hh99_actualproject.model.Member;
import com.sparta.hh99_actualproject.repository.ChatRoomRepository;
import com.sparta.hh99_actualproject.repository.MemberRepository;
import com.sparta.hh99_actualproject.util.SecurityUtil;
import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.sparta.hh99_actualproject.dto.ChatRoomDto.ChatRoomReqUpdateDto;
import static com.sparta.hh99_actualproject.dto.ChatRoomDto.ChatRoomReqRequestDto;


@Service
@RequiredArgsConstructor
public class ChatService {

    private final MemberRepository memberRepository;

    private final ChatRoomRepository chatRoomRepository;

    private final AwsS3Service awsS3Service;

    // OpenVidu 서버가 수신하는 URL
    @Value("${openvidu.url}")
    private String OPENVIDU_URL;

    // OpenVidu 서버와 공유되는 비밀
    @Value("${openvidu.secret}")
    private String OPENVIDU_SECRET;

    private OpenVidu openVidu;

    @PostConstruct
    public OpenVidu openVidu() {
        return openVidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
    }

    //고민러의 상담신청 요청 로직
    @Transactional
    public ChatRoomResponseDto createTokenReq(ChatRoomReqRequestDto requestDto) throws OpenViduJavaClientException, OpenViduHttpException {

        //로그인한 유저의 ID를 가져온다.
        String memberId = SecurityUtil.getCurrentMemberId();

        //로그인한 유저의 ID로 테이블을 찾아온다.
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));


        //고민러 테이블이 null이며 리스너 테이블이 null이 아니면 참가할 수 있는 방이 존재함을 의미한다.
        //위 조건에 따라 리스너가 이미 존재하는 방의 카테고리를 찾아 검색 , 입장 후 입장한 방의 sessionId , 새로운 token을 리턴한다.
        if (chatRoomRepository.findAllByReqNicknameIsNullAndResNicknameIsNotNull().size() != 0) {
            List<ChatRoom> resChatRoomList = chatRoomRepository.findAllByReqNicknameIsNullAndResNicknameIsNotNull();

            //조건에 맞게 랜덤매칭 , 랜덤매칭된 roomTable을 update , 매칭된 room의 sessionId를 리턴한다.
            //Db의 RoomId를 가져온다.
            String sessionId = registerReqChatRoom(requestDto, member, resChatRoomList);

            //이 사용자가 연결할 때 다른 사용자에게 전달할 선택적 데이터 , 유저의 닉네임을 전달할 것
            String serverData = "{\"serverData\": \"" + member.getNickname() + "\"}";

            // serverData 및 역할을 사용하여 connectionProperties 객체를 빌드합니다.
            ConnectionProperties connectionProperties = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC).data(serverData).build();

            //오픈비두에 활성화된 세션을 모두 가져와 리스트에 담는다.
            //활성화된 session의 sessionId들을 registerReqChatRoom에서 리턴한 sessionId(입장할 채팅방의 sessionId)와 비교
            //같을 경우 해당 session으로 새로운 토큰을 생성한다.
            openVidu.fetch();
            List<Session> activeSessionList = openVidu.getActiveSessions();
            Session session = registerGetSession(sessionId, activeSessionList);

            //생성된 connectionProperties와 추출된 기존의 session으로 새로운 연결을 생성합니다.
            //토큰을 가져옵니다.
            String token = session.createConnection(connectionProperties).getToken();

            return ChatRoomResponseDto.builder()
                    .sessionId(sessionId)
                    .token(token)
                    .role("request")
                    .build();
        }


        //고민러 테이블이 null이며 리스너 테이블이 null이면 참가할 수 있는 방이 존재하지 않음을 의미한다.
        //위 조건에 따라 새로운 방을 생성한다.
        if (chatRoomRepository.findAllByReqNicknameIsNullAndResNicknameIsNull().size() == 0) {

            //이 사용자가 연결할 때 다른 사용자에게 전달할 선택적 데이터 , 유저의 닉네임을 전달할 것
            String serverData = "{\"serverData\": \"" + member.getNickname() + "\"}";

            // serverData 및 역할을 사용하여 connectionProperties 객체를 빌드합니다.
            ConnectionProperties connectionProperties = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC).data(serverData).build();

            // 새로운 OpenVidu 세션 생성
            Session session = openVidu.createSession();

            //최근에 생성된 connectionProperties로 새로운 연결을 생성합니다.
            //토큰을 가져옵니다.
            String token = session.createConnection(connectionProperties).getToken();

            List<MultipartFile> imgList = requestDto.getImgList();

            List<String> imgPath = awsS3Service.uploadFile(imgList);

            //생성된 방에 입장하기위한 유저가 오픈비두에 활성화된 서버의 sessionId와
            //생성된 방의 sessionI가 같음을 비교 해당 방의 세션을 가져오기 위해
            //openvidu.getSessionId를 db에 저장한다.
            ChatRoom chatRoom = ChatRoom.builder()
                    .chatRoomId(session.getSessionId())
                    .reqTitle(requestDto.getReqTitle())
                    .reqCategory(requestDto.getReqCategory())
                    .reqGender(requestDto.getReqGender())
                    .reqNickname(member.getNickname())
                    .reqAge(member.getAge())
                    .reqLoveType(member.getLoveType())
                    .reqLovePeriod(member.getLovePeriod())
                    .imgUrl1(imgPath.get(0))
                    .imgUrl2(imgPath.get(1))
                    .imgUrl3(imgPath.get(2))
                    .member(member)
                    .build();

            chatRoomRepository.save(chatRoom);

            //리턴할 dto를 빌드한다.
            return ChatRoomResponseDto.builder()
                    .sessionId(session.getSessionId())
                    .token(token)
                    .role("request")
                    .build();
        }
        // 클라이언트에게 응답을 반환
        return null;
    }

    //상담러의 채팅신청 로직
    public ChatRoomResponseDto createTokenRes(ChatRoomResRequestDto requestDto) throws OpenViduJavaClientException, OpenViduHttpException {

        //로그인한 유저의 ID를 가져온다.
        String memberId = SecurityUtil.getCurrentMemberId();

        //로그인한 유저의 ID로 테이블을 찾아온다.
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        //리스너 테이블이 null이며 고민러 테이블이 null이 아니면 리스너가 참가할 수 있는 방이 존재함을 의미한다.
        //위 조건에 따라 리스너가 이미 존재하는 방의 카테고리를 찾아 검색 , 입장 후 입장한 방의 sessionId , 새로운 token을 리턴한다.
        if (chatRoomRepository.findAllByReqNicknameIsNotNullAndResNicknameIsNull().size() != 0) {
            List<ChatRoom> reqChatRoomList = chatRoomRepository.findAllByReqNicknameIsNotNullAndResNicknameIsNull();
            for (ChatRoom chatRoom : reqChatRoomList) {
                System.out.println("chatRoom = " + chatRoom.getReqCategory());
            }

            //조건에 맞게 랜덤매칭 , 랜덤매칭된 roomTable을 update , 매칭된 room의 sessionId를 리턴한다.
            //DB에 있는 RoomId를 가져온다.
            String sessionId = registerResChatRoom(requestDto, member, reqChatRoomList);
            System.out.println("sessionId = " + sessionId);

            //이 사용자가 연결할 때 다른 사용자에게 전달할 선택적 데이터 , 유저의 닉네임을 전달할 것
            String serverData = "{\"serverData\": \"" + member.getNickname() + "\"}";

            // serverData 및 역할을 사용하여 connectionProperties 객체를 빌드합니다.
            ConnectionProperties connectionProperties = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC).data(serverData).build();

            //오픈비두에 활성화된 세션을 모두 가져와 리스트에 담는다.
            //활성화된 session의 sessionId들을 registerReqChatRoom에서 리턴한 sessionId(입장할 채팅방의 sessionId)와 비교
            //같을 경우 해당 session으로 새로운 토큰을 생성한다.
            openVidu.fetch();
            List<Session> activeSessionList = openVidu.getActiveSessions();

            Session session = registerGetSession(sessionId, activeSessionList);

            //생성된 connectionProperties와 추출된 기존의 session으로 새로운 연결을 생성합니다.
            //토큰을 가져옵니다.
            String token = session.createConnection(connectionProperties).getToken();

            return ChatRoomResponseDto.builder()
                    .sessionId(sessionId)
                    .token(token)
                    .role("request")
                    .build();
        }


        //고민러 테이블이 null이며 리스너 테이블이 null이면 참가할 수 있는 방이 존재하지 않음을 의미한다.
        //위 조건에 따라 새로운 방을 생성한다.
        if (chatRoomRepository.findAllByReqNicknameIsNullAndResNicknameIsNull().size() == 0) {

            //이 사용자가 연결할 때 다른 사용자에게 전달할 선택적 데이터 , 유저의 닉네임을 전달할 것
            String serverData = "{\"serverData\": \"" + member.getNickname() + "\"}";

            // serverData 및 역할을 사용하여 connectionProperties 객체를 빌드합니다.
            ConnectionProperties connectionProperties = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC).data(serverData).build();

            // 새로운 OpenVidu 세션 생성
            Session session = openVidu.createSession();

            //최근에 생성된 connectionProperties로 새로운 연결을 생성합니다.
            //토큰을 가져옵니다.
            String token = session.createConnection(connectionProperties).getToken();


            //생성된 방에 입장하기위한 유저가 오픈비두에 활성화된 서버의 sessionId와
            //생성된 방의 sessionI가 같음을 비교 해당 방의 세션을 가져오기 위해
            //openvidu.getSessionId를 db에 저장한다.
            ChatRoom chatRoom = ChatRoom.builder()
                    .member(member)
                    .chatRoomId(session.getSessionId())
                    .resCategory(requestDto.getResCategory())
                    .resNickname(member.getNickname())
                    .resGender(member.getGender())
                    .resLoveType(member.getLoveType())
                    .resLovePeriod(member.getLovePeriod())
                    .build();

            chatRoomRepository.save(chatRoom);

            //리턴할 dto를 빌드한다.
            return ChatRoomResponseDto.builder()
                    .sessionId(session.getSessionId())
                    .token(token)
                    .role("request")
                    .build();
        }
        // 클라이언트에게 응답을 반환
        return null;
    }


    private Session registerGetSession(String sessionId, List<Session> activeSessionList) {
        Session session = null;
        for (int i = 0 ; i < activeSessionList.size() ; i ++) {
            if (activeSessionList.get(i).getSessionId().equals(sessionId)) {
                session = activeSessionList.get(i);
                return session;
            }
        }
        return session;
    }


    private String registerReqChatRoom(ChatRoomReqRequestDto requestDto, Member member, List<ChatRoom> ResChatRoomList) {
        String sessionId = null;

        for (ChatRoom chatRoom : ResChatRoomList) {
            if (chatRoom.getResCategory().equals(requestDto.getReqCategory()) ||
                    chatRoom.getResGender().equals(requestDto.getReqGender()) ||
                    chatRoom.getResCategory().equals("썸") ||
                    chatRoom.getResCategory().equals("고백") ||
                    chatRoom.getResCategory().equals("연애중") ||
                    chatRoom.getResCategory().equals("19") ||
                    chatRoom.getResCategory().equals("재회") ||
                    chatRoom.getResCategory().equals("이별") ||
                    chatRoom.getResCategory().equals("기타")) {

                chatRoom = ResChatRoomList.get(0);

                List<String> imgPathList = awsS3Service.uploadFile(requestDto.getImgList());

                ChatRoomReqUpdateDto chatRoomReqUpdateDto = ChatRoomReqUpdateDto.builder()
                        .imgUrl1(imgPathList.get(0))
                        .imgUrl2(imgPathList.get(1))
                        .imgUrl3(imgPathList.get(2))
                        .reqTitle(requestDto.getReqTitle())
                        .reqCategory(requestDto.getReqCategory())
                        .reqAge(member.getAge())
                        .reqGender(member.getGender())
                        .reqLovePeriod(member.getLovePeriod())
                        .reqNickname(member.getNickname())
                        .reqLoveType(member.getLoveType())
                        .build();
                chatRoom.reqUpdate(chatRoomReqUpdateDto);

                sessionId = chatRoom.getChatRoomId();
            }
        }
        return sessionId;
    }


    private String registerResChatRoom(ChatRoomResRequestDto requestDto, Member member, List<ChatRoom> ReqChatRoomList) {
        String sessionId = null;
        //리스너의 채팅 매칭 로직
        for (ChatRoom chatRoom : ReqChatRoomList) {
            if (chatRoom.getReqCategory().equals(requestDto.getResCategory())||
                    chatRoom.getReqCategory().equals("썸") ||
                    chatRoom.getReqCategory().equals("고백")||
                    chatRoom.getReqCategory().equals("연애중")||
                    chatRoom.getReqCategory().equals("19")  ||
                    chatRoom.getReqCategory().equals("재회") ||
                    chatRoom.getReqCategory().equals("이별") ||
                    chatRoom.getReqCategory().equals("기타")) {

                chatRoom = ReqChatRoomList.get(0);

                ChatRoomResUpdateDto chatRoomResUpdateDto = ChatRoomResUpdateDto.builder()
                        .resCategory(requestDto.getResCategory())
                        .resGender(member.getGender())
                        .resLovePeriod(member.getLovePeriod())
                        .resLoveType(member.getLoveType())
                        .resNickname(member.getNickname())
                        .build();

                chatRoom.resUpdate(chatRoomResUpdateDto);
                sessionId = chatRoom.getChatRoomId();
            }
        }
        return sessionId;
    }
}