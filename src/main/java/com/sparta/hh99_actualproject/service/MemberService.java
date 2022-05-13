package com.sparta.hh99_actualproject.service;


import com.sparta.hh99_actualproject.dto.EssentialInfoRequestDto;
import com.sparta.hh99_actualproject.dto.MemberInfoResponseDto;
import com.sparta.hh99_actualproject.dto.MemberInfoResponseDto.ChatHistoryReponseDto;
import com.sparta.hh99_actualproject.dto.MemberInfoResponseDto.PostListResponseDto;
import com.sparta.hh99_actualproject.dto.MemberRequestDto;
import com.sparta.hh99_actualproject.dto.TokenDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.jwt.TokenProvider;
import com.sparta.hh99_actualproject.model.Board;
import com.sparta.hh99_actualproject.model.ChatRoom;
import com.sparta.hh99_actualproject.model.Follow;
import com.sparta.hh99_actualproject.model.Member;
import com.sparta.hh99_actualproject.repository.FollowRepository;
import com.sparta.hh99_actualproject.repository.MemberRepository;
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
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MemberService {

    private MemberRepository memberRepository;
    private Validator validator;
    private PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final FollowRepository followRepository;

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

        //최종 리턴할 Dto를 미리 생성
        MemberInfoResponseDto memberInfoResponseDto = new MemberInfoResponseDto();

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
        List<Follow> followList = member.getFollowList();
        List<String> followMemeberList = new ArrayList<>();
        for (Follow follow : followList) {
                followMemeberList.add(follow.getFollowMemberId());
        }

        //
        memberInfoResponseDto = MemberInfoResponseDto.builder()
                .memberId(memberId)
                //merge 후 주석해제 , 추가 작업
                //.resTag()
                .reward(member.getReward())
                //merge 후 주석해제 쿼리로 빌드해야함
                //.score(member.getScore().getScore())
                .followList(followMemeberList)
//                .followerList()
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
