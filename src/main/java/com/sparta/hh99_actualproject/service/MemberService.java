package com.sparta.hh99_actualproject.service;


import com.sparta.hh99_actualproject.dto.EssentialInfoRequestDto;
import com.sparta.hh99_actualproject.dto.MemberInfoResponseDto;
import com.sparta.hh99_actualproject.dto.MemberInfoResponseDto.ChatHistoryReponseDto;
import com.sparta.hh99_actualproject.dto.MemberInfoResponseDto.MessageResponseDto;
import com.sparta.hh99_actualproject.dto.MemberInfoResponseDto.PostListResponseDto;
import com.sparta.hh99_actualproject.dto.MemberInfoResponseDto.ResTagResponseDto;
import com.sparta.hh99_actualproject.dto.MemberRequestDto;
import com.sparta.hh99_actualproject.dto.TokenDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.jwt.TokenProvider;
import com.sparta.hh99_actualproject.model.*;
import com.sparta.hh99_actualproject.repository.FollowRepository;
import com.sparta.hh99_actualproject.repository.MemberRepository;
import com.sparta.hh99_actualproject.repository.ResponseTagRepository;
import com.sparta.hh99_actualproject.repository.ScoreRepository;
import com.sparta.hh99_actualproject.service.validator.Validator;
import com.sparta.hh99_actualproject.util.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class MemberService {

    private MemberRepository memberRepository;
    private Validator validator;
    private PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final FollowRepository followRepository;
    private final ResponseTagRepository responseTagRepository;

    private final ScoreRepository scoreRepository;

    public boolean signup(MemberRequestDto memberRequestDto) {
        validator.validateSignUpInput(memberRequestDto);

        if (memberRepository.existsByMemberId(memberRequestDto.getMemberId()))
            throw new PrivateException(StatusCode.SIGNUP_MEMBER_ID_DUPLICATE_ERROR);

        Member member = Member.builder()
                .memberId(memberRequestDto.getMemberId())
                .nickname(memberRequestDto.getName())
                .password(passwordEncoder.encode(memberRequestDto.getPassword()))
                .build();

        memberRepository.save(member);

        return true;
    }

    public TokenDto login(MemberRequestDto memberRequestDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(memberRequestDto.getMemberId(), memberRequestDto.getPassword());

        //authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 loadUserByUsername 메서드가 실행 됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        //위의 결과값을 가지고 SecurityContext 에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenDto tokenDto;

        Member findedMember = memberRepository.findByMemberId(memberRequestDto.getMemberId())
                .orElseThrow(() -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        //TODO : refreshToken 구현 필요
        String accessToken = tokenProvider.createAccessToken(authentication.getName(), findedMember.getNickname());

        tokenDto = TokenDto.builder()
                .accessToken(accessToken)
                .build();

        return tokenDto;
    }

    public TokenDto updateMemberInfo(EssentialInfoRequestDto essentialInfoRequestDto) {
        validator.validateMemberInfoInput(essentialInfoRequestDto);

        Member findedMember = memberRepository.findByMemberId(SecurityUtil.getCurrentMemberId())
                .orElseThrow(() -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        findedMember.updateMemberEssentialInfo(essentialInfoRequestDto);

        memberRepository.save(findedMember);

        TokenDto tokenDto;

        //TODO : refreshToken 구현 필요
        String accessToken = tokenProvider.createAccessToken(findedMember.getMemberId(), findedMember.getNickname());

        tokenDto = TokenDto.builder()
                .accessToken(accessToken)
                .build();

        return tokenDto;
    }

    //리스트로 리턴되야하는 dto들의 set을 선행 , 나머지 객체들을 마지막에 빌드 후 리턴하자.
    public MemberInfoResponseDto getUserInfo(){
        String memberId = SecurityUtil.getCurrentMemberId();

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        //멤버의 채팅내역 추출 및 빌드
        List<ChatRoom> chatRoomList = member.getChatRoomList();
        //채팅 히스토리를 리턴할 Dto를 미리 생성
        List<ChatHistoryReponseDto> chatHistoryResponseDtoList = new ArrayList<>();
        //ChatRoom의 data중 return할 값들만들을 추출 -> 리스트로 만든다.
        //ChatHistory의 추출
        for (ChatRoom chatRoom : chatRoomList) {
            ChatHistoryReponseDto chatHistoryReponseDto = ChatHistoryReponseDto.builder()
                    .reqComment(chatRoom.getReqTitle())
                    .reqCategory(chatRoom.getReqCategory())
                    .createdAt(chatRoom.getMatchTime())
                    .build();

            if (member.getNickname().equals(chatRoom.getReqNickname())){
                chatHistoryReponseDto.setMyRole("request");
                chatHistoryReponseDto.setNickname(chatRoom.getResNickname());
                chatHistoryReponseDto.setColor(chatRoom.getResUserColor());
            }

            if (member.getNickname().equals(chatRoom.getResNickname())){
                chatHistoryReponseDto.setMyRole("response");
                chatHistoryReponseDto.setNickname(chatRoom.getReqNickname());
                chatHistoryReponseDto.setColor(chatRoom.getReqUserColor());
            }
            chatHistoryResponseDtoList.add(chatHistoryReponseDto);
        }

        //멤버 게시글을 모두 가져온다.
        //멤버의 게시글을 리턴형식에 맞게 build할 dto를 생성한다.
        //페이징으로 변환
        List<Board> boardList = member.getBoardList();
        List<PostListResponseDto> postListResponseDtoList = new ArrayList<>();
        for (Board board : boardList) {
            PostListResponseDto postListResponseDto = PostListResponseDto.builder()
                    .postId(board.getBoardPostId())
                    .createdAt(board.getCreatedAt().toString())
                    .title(board.getTitle())
                    .category(board.getCategory())
                    .comments(board.getCommentList().size())
                    .likes(board.getLikesList().size())
                    .build();
            postListResponseDtoList.add(postListResponseDto);
        }

        //멤버의 팔로우 유저 추출 및 빌드
        //followMemberId에는 내가 팔로우한 유저의 정보가 저장된다.
        //컬러가 추가되야할 것 같다.
        List<Follow> getFollowList = member.getFollowList();
        List<Follow> followList = new ArrayList<>();
        for (Follow follow : getFollowList) {
                followList.add(follow);
        }

        //멤버를 팔로워하는 유저 추출 및 빌드
        //followerMember는 나를 팔로워하는 멤버의 수가 들어간다.
        //멤버 테이블에 팔로우에 팔로우 멤버 아이디가 내 아이디인 유저
        List<Follow> getFollowerList = followRepository.findAllByFollowId(memberId);

        //멤버가 수신한 메시지를 가져올 것
        List<Message> messageList = member.getMessageList();
        List<MessageResponseDto> messageListResponseDtos = new ArrayList<>();
        for (Message getMessage : messageList) {
            MessageResponseDto messageResponseDto = MessageResponseDto.builder()
                    .createdAt(getMessage.getCreatedAt())
                    .reqMemberNickname(getMessage.getReqUserNickname())
                    .message(getMessage.getMessage())
                    .build();
            messageListResponseDtos.add(messageResponseDto);
        }

        //resTag 추출 로직
        //멤버가 획득한 response태그들을 찾아온다.
        List<ResponseTag> responseTagList = responseTagRepository.findAllByMemberId(memberId);

        //return값을 담을 Dto
        ResTagResponseDto resTagResponseDto = new ResTagResponseDto();

        //TagNumber별 리턴해야할 태그 값을 set해줄 Map
        ConcurrentHashMap<Integer , String> resTagMapContent = new ConcurrentHashMap<>();
        resTagMapContent.put(1 , "공감을 잘해줬어요");
        resTagMapContent.put(2 , "대화가 즐거웠어요");
        resTagMapContent.put(3 , "감수성이 풍부했어요");
        resTagMapContent.put(4 , "시원하게 팩트폭격을 해줘요");
        resTagMapContent.put(5 , "명쾌한 해결책을 알려줘요");

        //ResTag별로 인덱스를 지정하는 방법
        //1. 맵에 Res태그별 키값을 지정해준다.
        //2. value인 Res태그중 가장 큰 값을 두개 찾는다.
        //3. 가장 큰 값의 key 두개로 String맵의 key를 인덱스한다.
        ConcurrentHashMap<Integer , Integer> resTagIdx = new ConcurrentHashMap<>();

        for (ResponseTag responseTag : responseTagList) {
            resTagIdx.put(1 , responseTag.getResTag1Num());
            resTagIdx.put(2 , responseTag.getResTag2Num());
            resTagIdx.put(3 , responseTag.getResTag3Num());
            resTagIdx.put(4 , responseTag.getResTag4Num());
            resTagIdx.put(5 , responseTag.getResTag5Num());
        }

        //value를 기준으로 오름차순이 가능하게하는 comparingByValue함수를 사용하기위해
        //List에 Map.Entry로 resTagIdx를 할당해준다.
        //Map.Entry : Map을 For 문에서 돌려줄 경우 , Map에서 strem , 정렬 등을 필요할 때 사용하는 인터페이스
        //리스트의 Iterator와 비슷한 개념이라 생각하면 좋을 것 같다.
        //1. Map.Entry를 제네릭스로 받는 리스트 객체를 생성
        //2. 링크드 리스트 :
        //3. resTagIdx.map.entrySet() : 맵의 K , V 전체를 가져와서 리스트에 할당한다..
        List<Map.Entry<Integer, Integer>> entryList = new ArrayList<>(resTagIdx.entrySet());
        //Map.Entry.comparingByValue() : 해당 map의 value값을 기준으로 정렬한다.
        entryList.sort(Map.Entry.comparingByValue());


        //value값으로 정렬된 entryList의 3 , 4번째는 resTagIdx의 key 중 valye값이 가장 큰 키 2개를 의미
        //이는 resTag의 갯수가 가장 많은 것의 key를 의미한다.
        //해당 키로 resTag별로 미리 리턴 값(value)을 지정해준 map의 idx함으로써 가장 큰 값 두개의 String을 인덱스할 수 있다.
        resTagResponseDto.setResTag1(resTagMapContent.get(entryList.get(3).getKey()));
        resTagResponseDto.setResTag2(resTagMapContent.get(entryList.get(4).getKey()));

        //score에 memberId로 해당 멤버의 score를 찾아온다.
        Score score = scoreRepository.findByMemberId(memberId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_SCORE));

        MemberInfoResponseDto memberInfoResponseDto = MemberInfoResponseDto.builder()
                .memberId(memberId)
                .resTags(resTagResponseDto)
                .score(score.getScore())
                .reward(member.getReward())
                .messageList(messageListResponseDtos)
                .followList(followList)
                .followerList(getFollowerList.size())
                .chatHistory(chatHistoryResponseDtoList)
                .postList(postListResponseDtoList)
                .build();

        //좋아요는 length해서 숫자로만 내려드릴 것
        return memberInfoResponseDto;
    }


    public void checkMemberId(String memberId) {
        if (memberRepository.existsByMemberId(memberId))
            throw new PrivateException(StatusCode.SIGNUP_MEMBER_ID_DUPLICATE_ERROR);
    }

    public void checkNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname))
            throw new PrivateException(StatusCode.SIGNUP_NICKNAME_DUPLICATE_ERROR);
    }
}
