package com.sparta.hh99_actualproject.service;


import com.sparta.hh99_actualproject.dto.*;
import com.sparta.hh99_actualproject.dto.BoardResponseDto.PostListResponseDto;
import com.sparta.hh99_actualproject.dto.ChatRoomDto.ChatHistoryResponseDto;
import com.sparta.hh99_actualproject.dto.FollowResponseDto.MemebrInfoFollowResponseDto;
import com.sparta.hh99_actualproject.dto.MemberResponseDto.ResTagResponseDto;
import com.sparta.hh99_actualproject.dto.MessageDto.MemberInfoMessageResponseDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.jwt.TokenProvider;
import com.sparta.hh99_actualproject.model.*;
import com.sparta.hh99_actualproject.repository.*;
import com.sparta.hh99_actualproject.service.validator.Validator;
import com.sparta.hh99_actualproject.util.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final BoardRepository boardRepository;


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

        //authenticate 메서드가 실행이 될 때 CustomMemberDetailsService 에서 loadMemberByMembername 메서드가 실행 됨
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

    public MemberResponseDto getMemberProfile(){
        String memberId = SecurityUtil.getCurrentMemberId();

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        //멤버를 팔로워하는 유저 추출 및 빌드
        //followerMemberList는 나를 팔로워하는 멤버의 수가 들어간다.
        //멤버 테이블에 팔로우에 팔로우 멤버 아이디가 내 아이디인 유저
        List<Follow> getFollowerList = followRepository.findAllByFollowMemberId(memberId);

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
        //2. resTagIdx.map.entrySet() : 맵의 K , V 전체를 가져와서 리스트에 할당한다..
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


        return MemberResponseDto.builder()
                .memberId(memberId)
                .nickname(member.getNickname())
                .color(member.getColor())
                .lovePeriod(member.getLovePeriod())
                .loveType(member.getLoveType())
                .age(member.getAge())
                .dating(null)
                .resTags(resTagResponseDto)
                .score(score.getScore())
                .reward(member.getReward())
                .follower(getFollowerList.size())
                .build();
    }

    public List<ChatHistoryResponseDto> getMemberChatHistory() {
        String memberId = SecurityUtil.getCurrentMemberId();

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        //멤버의 채팅내역 추출 및 빌드
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByMemberMemberIdOrderByCreatedAtDesc(memberId);

        //채팅 히스토리를 리턴할 Dto를 미리 생성
        List<ChatHistoryResponseDto> chatHistoryResponseDtoList = new ArrayList<>();

        //ChatRoom의 data중 return할 값들만들을 추출 -> 리스트로 만든다.
        //ChatHistory의 추출
        for (ChatRoom chatRoom : chatRoomList) {
            ChatHistoryResponseDto chatHistoryReponseDto = ChatHistoryResponseDto.builder()
                    .reqComment(chatRoom.getReqTitle())
                    .reqCategory(chatRoom.getReqCategory())
                    .createdAt(chatRoom.getMatchTime())
                    .build();

            if (member.getNickname().equals(chatRoom.getReqNickname())){
                chatHistoryReponseDto.setMyRole("request");
                chatHistoryReponseDto.setNickname(chatRoom.getResNickname());
                chatHistoryReponseDto.setColor(chatRoom.getResMemberColor());
            }else if (member.getNickname().equals(chatRoom.getResNickname())){
                chatHistoryReponseDto.setMyRole("response");
                chatHistoryReponseDto.setNickname(chatRoom.getReqNickname());
                chatHistoryReponseDto.setColor(chatRoom.getReqMemberColor());
            }
            chatHistoryResponseDtoList.add(chatHistoryReponseDto);

            if (chatHistoryResponseDtoList.size() == 6){
                break;
            }
        }

        return chatHistoryResponseDtoList;
    }

    public List<MemberInfoMessageResponseDto> getMemberMessage(int page) {
        String memberId = SecurityUtil.getCurrentMemberId();
        PageRequest pageRequest = PageRequest.of(page-1 , 3);

        //멤버가 수신한 메시지를 가져올 것
        Page<Message> messageList = messageRepository.findAllByMemberMemberIdOrderByCreatedAt(memberId , pageRequest);

        List<MemberInfoMessageResponseDto> messageListResponseDtos = new ArrayList<>();

        for (Message getMessage : messageList) {
            MemberInfoMessageResponseDto messageResponseDto = MemberInfoMessageResponseDto.builder()
                    .messageId(getMessage.getMessageId())
                    .createdAt(getMessage.getCreatedAt())
                    .reqMemberNickname(getMessage.getReqMemberNickname())
                    .message(getMessage.getMessage())
                    .totalPages(messageList.getTotalPages())
                    .nextOrLastPageable(messageList.getPageable())
                    .build();
            messageListResponseDtos.add(messageResponseDto);
        }

        return messageListResponseDtos;
    }

    public List<MemebrInfoFollowResponseDto> getMemberFollow(int page) {
        //멤버의 팔로우 유저 추출 및 빌드
        String memberId = SecurityUtil.getCurrentMemberId();

        PageRequest pageRequest = PageRequest.of(page-1 , 5);

        Page<Follow> getFollowList = followRepository.findAllByMemberMemberIdOrderByCreatedAt(memberId, pageRequest);

        List<MemebrInfoFollowResponseDto> followList = new ArrayList<>();



        for (Follow follow : getFollowList) {
            MemebrInfoFollowResponseDto followResponseDto = MemebrInfoFollowResponseDto.builder()
                    .createdAt(String.valueOf(follow.getCreatedAt()))
                    .nickname(follow.getNickname())
                    .color(follow.getColor())
                    .totalPages(getFollowList.getTotalPages())
                    .nextOrLastPageable(getFollowList.nextOrLastPageable())
                    .build();
            followList.add(followResponseDto);
        }

        return followList;
    }

    public List<PostListResponseDto> getMemberBoard(int page) {
        String memberId = SecurityUtil.getCurrentMemberId();

        //멤버 게시글을 모두 가져온다.
        //멤버의 게시글을 리턴형식에 맞게 build할 dto를 생성한다.
        PageRequest pageRequest = PageRequest.of(page-1 , 8);

        Page<Board> boardList = boardRepository.findAllByMemberMemberIdOrderByCreatedAtDesc(memberId , pageRequest);

        List<PostListResponseDto> postListResponseDtoList = new ArrayList<>();
        Integer totalPages = boardList.getTotalPages();
        Pageable nextOrLastPageable = boardList.nextOrLastPageable();

        for (Board board : boardList) {
            PostListResponseDto postListResponseDto = PostListResponseDto.builder()
                    .postId(board.getBoardPostId())
                    .createdAt(board.getCreatedAt().toString())
                    .title(board.getTitle())
                    .category(board.getCategory())
                    .totalPages(totalPages)
                    .nextOrLastPageable(nextOrLastPageable)
                    .comments(board.getCommentList().size())
                    .likes(board.getLikesList().size())
                    .build();
            postListResponseDtoList.add(postListResponseDto);
        }
        return postListResponseDtoList;
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
