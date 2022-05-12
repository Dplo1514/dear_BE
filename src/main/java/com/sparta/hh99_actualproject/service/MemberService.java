package com.sparta.hh99_actualproject.service;


import com.sparta.hh99_actualproject.dto.*;
import com.sparta.hh99_actualproject.dto.MemberInfoResponseDto.ChatHistoryReponseDto;
import com.sparta.hh99_actualproject.dto.MemberInfoResponseDto.PostLikesResponseDto;
import com.sparta.hh99_actualproject.dto.MemberInfoResponseDto.PostListResponseDto;
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
        List<ChatHistoryReponseDto> chatHistoryReponseDtoList = new ArrayList<>();
        MemberInfoResponseDto memberInfoResponseDto = new MemberInfoResponseDto();

        //ChatRoom의 data들중 return할 값들만들을 추출 -> 리스트로 만든다.
        for (ChatRoom chatRoom : chatRoomList) {
            ChatHistoryReponseDto chatHistoryReponseDto = ChatHistoryReponseDto.builder()
                    .reqComment(chatRoom.getReqTitle())
                    .reqCategory(chatRoom.getReqCategory())
                    .createdAt(chatRoom.getMatchTime())
                    .chatTime(chatRoom.getChatTime())
                    .build();

            if (member.getNickname().equals(chatRoom.getReqNickname())){
                chatHistoryReponseDto.setMyRole("request");
                chatHistoryReponseDto.setOtherUser(chatRoom.getResNickname());
//                chatHistoryReponseDto.setOtherUserColor();
            }

            if (member.getNickname().equals(chatRoom.getResNickname())){
                chatHistoryReponseDto.setMyRole("response");
            }
            chatHistoryReponseDtoList.add(chatHistoryReponseDto);
        }
        //리턴할 dto에 빌드한다.
        memberInfoResponseDto.setChatHistory(chatHistoryReponseDtoList);

        //멤버의 게시글 내역 추출 및 빌드
        List<Board> boardList = member.getBoardList();
        List<PostListResponseDto> postListResponseDtoList = new ArrayList<>();
        for (Board board : boardList) {
            PostListResponseDto postListResponseDto = PostListResponseDto.builder()
                    .postId(board.getBoardPostId())
                    .createdAt(board.getCreatedAt().toString())
                    .title(board.getTitle())
                    .category(board.getCategory())
                    .imageUrl(board.getImgList().get(0).getImgUrl())
                    //숫자로 내려드리는게 좋을지 질문 후 작업
                    //댓글 수도 숫자로만 내려드릴 것
                    //.likesList(board.getLikesList().size())
                    .build();
            postListResponseDtoList.add(postListResponseDto);
        }
        memberInfoResponseDto.setPostList(postListResponseDtoList);

        //멤버의 팔로우 유저 추출 및 빌드
        //followMemberId에는 내가 팔로우한 유저의 정보가 저장된다.
        List<Follow> followList = member.getFollowList();
        List<String> followMemeberList = new ArrayList<>();
        for (Follow follow : followList) {
                followMemeberList.add(follow.getFollowMemberId());
        }


        //멤버의 팔로워 유저 추출 및 빌드
        //로그인한 유저의 멤버 Id가 followMemberId로 등록돼있는 멤버들을 찾아오면 나를 팔로우하는 유저를 찾아올 수 있다.
        List<String> followerMemeberList = new ArrayList<>();

        memberInfoResponseDto = MemberInfoResponseDto.builder()
                .memberId(memberId)
                //merge 후 주석해제 , 추가 작업
                //.resTag()
                .reward(member.getReward())
                //merge 후 주석해제 쿼리로 빌드해야함
                //.score(member.getScore().getScore())
                .followList(followMemeberList)
                //.followerList()
                .chatHistory(chatHistoryReponseDtoList)
                .postList(postListResponseDtoList)
                .build();

        //좋아요는 length해서 숫자로만 내려드릴 것
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
