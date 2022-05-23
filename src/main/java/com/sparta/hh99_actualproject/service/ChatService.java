package com.sparta.hh99_actualproject.service;

import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.ChatExtend;
import com.sparta.hh99_actualproject.model.ChatRoom;
import com.sparta.hh99_actualproject.model.Member;
import com.sparta.hh99_actualproject.repository.ChatExtendRepository;
import com.sparta.hh99_actualproject.repository.ChatRoomRepository;
import com.sparta.hh99_actualproject.repository.MemberRepository;
import com.sparta.hh99_actualproject.service.validator.Validator;
import com.sparta.hh99_actualproject.util.SecurityUtil;
import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.sparta.hh99_actualproject.dto.ChatRoomDto.*;


@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatExtendRepository chatExtendRepository;

    private final MemberRepository memberRepository;

    private final ChatRoomRepository chatRoomRepository;

    private final AwsS3Service awsS3Service;

    private final Validator validator;

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
    public ChatRoomMatchResponseDto createTokenReq(ChatRoomReqRequestDto requestDto) throws OpenViduJavaClientException, OpenViduHttpException {
        validator.hasNullChekckReqChat(requestDto);

        //로그인한 유저의 ID를 가져온다.
        String memberId = SecurityUtil.getCurrentMemberId();

        //로그인한 유저의 ID로 테이블을 찾아온다.
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        if (member.getReward() == null || member.getReward() < 1) {
            throw new PrivateException(StatusCode.WRONG_START_CHAT);
        }

        //고민러 테이블이 null이며 리스너 테이블이 null이 아니면 참가할 수 있는 방이 존재함을 의미한다.
        //위 조건에 따라 리스너가 이미 존재하는 방의 카테고리를 찾아 검색 , 입장 후 입장한 방의 sessionId , 새로운 token을 리턴한다.
        if (chatRoomRepository.findAllByReqNicknameIsNullAndResNicknameIsNotNull().size() != 0) {
            List<ChatRoom> resChatRoomList = chatRoomRepository.findAllByReqNicknameIsNullAndResNicknameIsNotNull();

            //조건에 맞게 랜덤매칭 , 랜덤매칭된 roomTable을 update , 매칭된 room의 sessionId를 리턴한다.
            //Db의 RoomId를 가져온다.
            String sessionId = registerReqChatRoom(requestDto, member, resChatRoomList);

            //채팅방에 sessionId로 오픈비두의 활성화된 세션을 찾아 토큰을 발급합니다.
            String token = registerEnterChatRoom(member, sessionId);

            return ChatRoomMatchResponseDto.builder()
                    .sessionId(sessionId)
                    .token(token)
                    .role("request")
                    .build();
        }


        //고민러 테이블이 null이며 리스너 테이블이 null이면 참가할 수 있는 방이 존재하지 않음을 의미한다.
        //위 조건에 따라 새로운 방을 생성한다.
        if (chatRoomRepository.findAllByReqNicknameIsNullAndResNicknameIsNull().size() == 0) {

            ChatRoomMatchResponseDto newToken = createNewToken(member);

            //생성된 방에 입장하기위한 유저가 오픈비두에 활성화된 서버의 sessionId와
            //생성된 방의 sessionI가 같음을 비교 해당 방의 세션을 가져오기 위해
            //openvidu.getSessionId를 db에 저장한다.
            ChatRoom chatRoom = ChatRoom.builder()
                    .chatRoomId(newToken.getSessionId())
                    .reqMemberId(member.getMemberId())
                    .reqTitle(requestDto.getReqTitle())
                    .reqCategory(requestDto.getReqCategory())
                    .reqGender(requestDto.getReqGender())
                    .reqNickname(member.getNickname())
                    .reqAge(member.getAge())
                    .reqLoveType(member.getLoveType())
                    .reqLovePeriod(member.getLovePeriod())
                    .reqMemberColor(member.getColor())
                    .reqMemberDating(member.getDating())
                    .member(member)
                    .build();

            saveImg(requestDto, chatRoom);

            chatRoomRepository.save(chatRoom);
            newToken.setRole("request");
            //리턴할 dto를 빌드한다.
            return newToken;
        }

        // 클라이언트에게 응답을 반환
        return null;
    }


    //상담러의 채팅신청 로직
    @Transactional
    public ChatRoomMatchResponseDto createTokenRes(ChatRoomResRequestDto requestDto) throws OpenViduJavaClientException, OpenViduHttpException {
        validator.hasNullChekckResChat(requestDto);

        //로그인한 유저의 ID를 가져온다.
        String memberId = SecurityUtil.getCurrentMemberId();

        //로그인한 유저의 ID로 테이블을 찾아온다.
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        //리스너 테이블이 null이며 고민러 테이블이 null이 아니면 리스너가 참가할 수 있는 방이 존재함을 의미한다.
        //위 조건에 따라 리스너가 이미 존재하는 방의 카테고리를 찾아 검색 , 입장 후 입장한 방의 sessionId , 새로운 token을 리턴한다.
        if (chatRoomRepository.findAllByReqNicknameIsNotNullAndResNicknameIsNull().size() != 0) {
            List<ChatRoom> reqChatRoomList = chatRoomRepository.findAllByReqNicknameIsNotNullAndResNicknameIsNull();

            //조건에 맞게 랜덤매칭 , 랜덤매칭된 roomTable을 update , 매칭된 room의 sessionId를 리턴한다.
            //DB에 있는 RoomId를 가져온다.
            String sessionId = registerResChatRoom(requestDto, member, reqChatRoomList);

            System.out.println("sessionId = " + sessionId);

            //채팅방에 sessionId로 오픈비두의 활성화된 세션을 찾아 토큰을 발급합니다.
            //토큰을 가져옵니다.
            String token = registerEnterChatRoom(member, sessionId);

            return ChatRoomMatchResponseDto.builder()
                    .sessionId(sessionId)
                    .token(token)
                    .role("response")
                    .build();
        }


        //고민러 테이블이 null이며 리스너 테이블이 null이면 참가할 수 있는 방이 존재하지 않음을 의미한다.
        //위 조건에 따라 새로운 방을 생성한다.
        if (chatRoomRepository.findAllByReqNicknameIsNullAndResNicknameIsNull().size() == 0) {

            ChatRoomMatchResponseDto newToken = createNewToken(member);

            //생성된 방에 입장하기위한 유저가 오픈비두에 활성화된 서버의 sessionId와
            //생성된 방의 sessionI가 같음을 비교 해당 방의 세션을 가져오기 위해
            //openvidu.getSessionId를 db에 저장한다.
            ChatRoom chatRoom = ChatRoom.builder()
                    .member(member)
                    .chatRoomId(newToken.getSessionId())
                    .resMemberId(member.getMemberId())
                    .resCategory(requestDto.getResCategory())
                    .resNickname(member.getNickname())
                    .resGender(member.getGender())
                    .resLoveType(member.getLoveType())
                    .resLovePeriod(member.getLovePeriod())
                    .resAge(member.getAge())
                    .resMemberColor(member.getColor())
                    .resMemberDating(member.getDating())
                    .build();

            chatRoomRepository.save(chatRoom);
            newToken.setRole("response");
            return newToken;
        }

        // 클라이언트에게 응답을 반환
        return null;
    }

    //채팅방 리턴하기
    @Transactional
    public ChatRoomResponseDto getRoomData(String sessionId) {
        ChatRoom chatRoom = chatRoomRepository.findById(sessionId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_CHAT_ROOM));

        List<String> ResponseImgUrl = new ArrayList<>();

        builderImgUrlList(chatRoom, ResponseImgUrl);


        return ChatRoomResponseDto.builder()
                .category(chatRoom.getReqCategory())
                .reqMemberId(chatRoom.getReqMemberId())
                .reqAge(chatRoom.getReqAge())
                .reqGender(chatRoom.getReqGender())
                .reqLovePeriod(chatRoom.getReqLovePeriod())
                .reqLoveType(chatRoom.getReqLoveType())
                .reqNickname(chatRoom.getReqNickname())
                .reqTitle(chatRoom.getReqTitle())
                .reqColor(chatRoom.getReqMemberColor())
                .reqUserDating(chatRoom.getReqMemberDating())
                .resMemberId(chatRoom.getResMemberId())
                .resAge(chatRoom.getResAge())
                .resGender(chatRoom.getResGender())
                .resLovePeriod(chatRoom.getResLovePeriod())
                .resLoveType(chatRoom.getResLoveType())
                .resNickname(chatRoom.getResNickname())
                .resColor(chatRoom.getResMemberColor())
                .resUserDating(chatRoom.getResMemberDating())
                .imageUrl(ResponseImgUrl)
                .build();
    }

    //채팅 연장하기
    @Transactional
    public boolean extendChat(String chatRoomId) {
        String memberId = SecurityUtil.getCurrentMemberId();

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_CHAT_ROOM));

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        boolean agree = false;

        //해당 채팅방의 ChatExtend가 null이 아니면 유저의 연장의사를 업데이트한다.
        if (chatRoom.getChatExtend() != null) {
            ChatExtend chatExtend = chatRoom.getChatExtend();

            //해당 채팅방의 ChatExtend가 6일 때에 더 이상 채팅시간의 연장이 불가능함을 의미 exception을 발생시킨다.
            validator.hasValidCheckExtend(chatRoom);

            //member의 닉네임이 req닉네임과 일치하면 시간 연장 요청을 보낸 멤버가 req임을 의미한다.
            //1. Member의 Role을 체크 -> update
            //2. 두명의 연장의사 동의 여부를 체크 후 true , false를 리턴한다.
            agree = isCheckExtendMemberRoleAndUpdate(memberId, chatRoom, member, chatExtend);

            return agree;

        } else {
            //해당 채팅방의 chatExtend가 null이면 채팅방에 chatExtend를 저장해줘야한다.
            //member의 닉네임이 req닉네임과 일치하면 시간 연장 요청을 보낸 멤버가 req임을 의미한다.
            //req Member의 연장의사 Column을 빌드한다.
            isCheckExtendMemberRoleAndSave(memberId, chatRoom, member);
        }

        return agree;
    }


    //리워드 적립
    @Transactional
    public void stackReward(String sessionId, String terminationTime) {
        ChatRoom chatRoom = chatRoomRepository.findById(sessionId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_CHAT_ROOM));

        //채팅방의 닉네임을 활용해 request유저와 response유저를 찾아온다.
        Member reqMember = memberRepository.findByNickname(chatRoom.getReqNickname()).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        Member resMember = memberRepository.findByNickname(chatRoom.getResNickname()).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        //받아온 종료시간을 dateTime으로 형변환
        LocalDateTime terminationDateTime = LocalDateTime.parse(terminationTime, DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));

        //dn에서 가져온 매칭 시간 = 채팅이 시작된 시간을 datetime으로 형변환
        LocalDateTime startChatTime = LocalDateTime.parse(chatRoom.getMatchTime(), DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));


        //만약 두 시간의 날짜가 다르면 자정이 지났음을 의미 1시간을 minus함으로써 시간의 비교가 가능해진다.
        if (terminationDateTime.getDayOfWeek() != startChatTime.getDayOfWeek()) {
            terminationDateTime = terminationDateTime.minusHours(1);
        }

        //종료시간에서 시작시간을 차감해 채팅시간을 구한다.
        LocalDateTime chatTime = terminationDateTime.minusHours(startChatTime.getHour()).minusMinutes(startChatTime.getMinute());

        //채팅시간이 3분보다 크면 req멤버의 리워드의 차감이 일어난다.
        //채팅시간이 7분보다 크면 res멤버의 리워드의 적립이 일어난다.
        if (chatTime.getMinute() > 1) {
            reqMember.setReward(reqMember.getReward() - 1);
        }

        if (chatTime.getMinute() > 5) {
            resMember.setReward(resMember.getReward() + 1);
        }
    }

    //채팅방이 매치되지않고 종료시 채팅방을 삭제한다.
    public void disconnectChat(String sessionId) {
        ChatRoom chatRoom = chatRoomRepository.findById(sessionId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_CHAT_ROOM));
        chatRoomRepository.delete(chatRoom);
    }


    //메서드
    //채팅방 생성시 토큰을 발급한다.
    private ChatRoomMatchResponseDto createNewToken(Member member) throws OpenViduJavaClientException, OpenViduHttpException {

        //이 사용자가 연결할 때 다른 사용자에게 전달할 선택적 데이터 , 유저의 닉네임을 전달할 것
        String serverData = member.getNickname();

        // serverData 및 역할을 사용하여 connectionProperties 객체를 빌드합니다.
        ConnectionProperties connectionProperties = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC).data(serverData).build();

        // 새로운 OpenVidu 세션 생성
        Session session = openVidu.createSession();

        //1번 session
        //1번 session -> 유저1 입장신청 -> 오픈비두에서 session1에 유저 1을 등록해줍니다.
        //2번 유저 -> 1번 session -> 유저2 입장신청 -> 오픈비두에서 session1에 유저 2를 등록해줍니다.

        //최근에 생성된 connectionProperties로 새로운 연결을 생성합니다.
        //토큰을 가져옵니다.
        String token = session.createConnection(connectionProperties).getToken();

        return ChatRoomMatchResponseDto.builder()
                .sessionId(session.getSessionId())
                .token(token)
                .build();
    }

    //채팅방 입장시 token을 발급한다.
    private String registerEnterChatRoom(Member member, String sessionId) throws OpenViduJavaClientException, OpenViduHttpException {
        String serverData = member.getNickname();

        //serverData 및 역할을 사용하여 connectionProperties 객체를 빌드합니다.
        ConnectionProperties connectionProperties = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC).data(serverData).build();

        //오픈비두에 활성화된 세션을 모두 가져와 리스트에 담는다.
        //활성화된 session의 sessionId들을 registerReqChatRoom에서 리턴한 sessionId(입장할 채팅방의 sessionId)와 비교
        //같을 경우 해당 session으로 새로운 토큰을 생성한다.
        openVidu.fetch();

        List<Session> activeSessionList = openVidu.getActiveSessions();

        Session session = null;

        for (Session getSession : activeSessionList) {
            if (getSession.getSessionId().equals(sessionId)) {
                session = getSession;
            }
        }

        //토큰을 가져옵니다.
        return session.createConnection(connectionProperties).getToken();
    }

    //고민러의 채팅 매칭 로직
    private String registerReqChatRoom(ChatRoomReqRequestDto requestDto, Member member, List<ChatRoom> ResChatRoomList) {
        String sessionId = null;

        ArrayList<String> matchCategory = new ArrayList<>(Arrays.asList("솔로", "썸", "짝사랑", "연애", "이별", "기타"));

        for (ChatRoom chatRoom : ResChatRoomList) {
            if (chatRoom.getResCategory().equals(requestDto.getReqCategory()) ||
                    chatRoom.getResGender().equals(requestDto.getReqGender()) ||
                    matchCategory.contains(requestDto.getReqCategory())) {

                chatRoom = ResChatRoomList.get(0);

                saveImg(requestDto, chatRoom);

                LocalDateTime now = LocalDateTime.now();
                String matchTime = now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));

                ChatRoomReqUpdateDto chatRoomReqUpdateDto = ChatRoomReqUpdateDto.builder()
                        .reqMemberId(member.getMemberId())
                        .reqTitle(requestDto.getReqTitle())
                        .reqCategory(requestDto.getReqCategory())
                        .reqAge(member.getAge())
                        .reqGender(member.getGender())
                        .reqLovePeriod(member.getLovePeriod())
                        .reqNickname(member.getNickname())
                        .reqLoveType(member.getLoveType())
                        .reqUserColor(member.getColor())
                        .reqUserDating(member.getDating())
                        .matchTime(matchTime)
                        .build();

                chatRoom.reqUpdate(chatRoomReqUpdateDto);
                sessionId = chatRoom.getChatRoomId();
            }
        }
        return sessionId;
    }

    //리스너의 채팅 매칭 로직
    private String registerResChatRoom(ChatRoomResRequestDto requestDto, Member member, List<ChatRoom> ReqChatRoomList) {
        String sessionId = null;

        ArrayList<String> matchCategory = new ArrayList<>(Arrays.asList("솔로", "썸", "짝사랑", "연애", "이별", "기타"));

        System.out.println("matchCategory = " + requestDto.getResCategory());


        //리스너의 채팅 매칭 로직
        for (ChatRoom chatRoom : ReqChatRoomList) {
            if (chatRoom.getReqCategory().equals(requestDto.getResCategory()) ||
                    matchCategory.contains(requestDto.getResCategory())) {

                chatRoom = ReqChatRoomList.get(0);

                LocalDateTime now = LocalDateTime.now();
                String matchTime = now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));

                ChatRoomResUpdateDto chatRoomResUpdateDto = ChatRoomResUpdateDto.builder()
                        .resMemberId(member.getMemberId())
                        .resCategory(requestDto.getResCategory())
                        .resGender(member.getGender())
                        .resLovePeriod(member.getLovePeriod())
                        .resLoveType(member.getLoveType())
                        .resNickname(member.getNickname())
                        .resAge(member.getAge())
                        .resUserColor(member.getColor())
                        .resUserDating(member.getDating())
                        .matchTime(matchTime)
                        .build();
                chatRoom.resUpdate(chatRoomResUpdateDto);
                sessionId = chatRoom.getChatRoomId();

                break;
            }
        }
        return sessionId;
    }

    //채팅방의 이미지 저장로직
    private void saveImg(ChatRoomReqRequestDto requestDto, ChatRoom chatRoom) {
        if (requestDto.getImgList() != null) {
            List<String> imgPath = awsS3Service.uploadFiles(requestDto.getImgList());

            if (imgPath.size() == 1) {
                chatRoom.setImgUrl1(imgPath.get(0));
            }

            if (imgPath.size() == 2) {
                chatRoom.setImgUrl1(imgPath.get(0));
                chatRoom.setImgUrl2(imgPath.get(1));
            }

            if (imgPath.size() == 3) {
                chatRoom.setImgUrl1(imgPath.get(0));
                chatRoom.setImgUrl2(imgPath.get(1));
                chatRoom.setImgUrl3(imgPath.get(2));
            }
        }
    }

    //채팅방의 이미지 url을 빌드해주는 로직
    private void builderImgUrlList(ChatRoom chatRoom, List<String> ResponseImgUrl) {
        if (chatRoom.getImgUrl1() != null) {
            ResponseImgUrl.add(chatRoom.getImgUrl1());
        }
        if (chatRoom.getImgUrl2() != null) {
            ResponseImgUrl.add(chatRoom.getImgUrl2());
        }
        if (chatRoom.getImgUrl3() != null) {
            ResponseImgUrl.add(chatRoom.getImgUrl3());
        }
    }

    //member의 닉네임이 req닉네임과 일치하면 시간 연장 요청을 보낸 멤버가 req임을 의미한다.
    private boolean isCheckExtendMemberRoleAndUpdate(String memberId, ChatRoom chatRoom, Member member, ChatExtend chatExtend) {

        if (member.getNickname().equals(chatRoom.getReqNickname())) {
            chatExtend.setChatRoom(chatRoom);
            chatExtend.setReqMemberId(memberId);

            //유저 두명의 연장 동의 check 및 true , false를 리턴
            resetCheckExtend(chatExtend);
        }

        if (member.getNickname().equals(chatRoom.getResNickname())) {
            chatExtend.setChatRoom(chatRoom);
            chatExtend.setResMemberId(memberId);

            //유저 두명의 연장 동의 check 및 true , false를 리턴
            resetCheckExtend(chatExtend);
        }
        return false;
    }

    private void isCheckExtendMemberRoleAndSave(String memberId, ChatRoom chatRoom, Member member) {

        //member의 닉네임이 req닉네임과 일치하면 시간 연장 요청을 보낸 멤버의 Role이 req임을 의미한다.
        //req Member의 연장의사 Column을 빌드하고 저장한다.
        if (member.getNickname().equals(chatRoom.getReqNickname())) {

            ChatExtend chatExtend = ChatExtend.builder()
                    .chatRoom(chatRoom)
                    .reqMemberId(memberId)
                    .build();

            chatExtend = chatExtendRepository.save(chatExtend);

            chatRoom.setChatExtend(chatExtend);
        }

        //member의 닉네임이 res닉네임과 일치하면 시간 연장 요청을 보낸 멤버의 Role이 res임을 의미한다.
        //res Member의 연장의사 Column을 빌드하고 저장한다.
        if (member.getNickname().equals(chatRoom.getResNickname())) {

            ChatExtend chatExtend = ChatExtend.builder()
                    .chatRoom(chatRoom)
                    .resMemberId(memberId)
                    .build();

            chatExtend = chatExtendRepository.save(chatExtend);

            chatRoom.setChatExtend(chatExtend);
        }
    }

    //유저 두명의 연장 동의 check 및 true , false를 리턴
    private boolean resetCheckExtend(ChatExtend chatExtend) {
        if (chatExtend.getReqMemberId() != null && chatExtend.getResMemberId() != null) {
            //chatExtend의 연장 횟수를 ++ , 위 두개 컬럼을 null로 변환함으로써
            //해당 채팅방의 연장횟수를 기억함으로써 6회 이상 연장되지 못하도록 제약을 걸어줄 수 있다.
            chatExtend.setReqMemberId(null);
            chatExtend.setResMemberId(null);
            chatExtend.setExtendCount(chatExtend.getExtendCount() + 1);
            return true;
        } else {
            return false;
        }
    }

}