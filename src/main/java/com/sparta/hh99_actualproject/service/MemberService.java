package com.sparta.hh99_actualproject.service;


import com.sparta.hh99_actualproject.dto.*;
import com.sparta.hh99_actualproject.dto.BoardResponseDto.PostListResponseDto;
import com.sparta.hh99_actualproject.dto.ChatRoomDto.ChatHistoryResponseDto;
import com.sparta.hh99_actualproject.dto.FollowResponseDto.MemebrInfoFollowResponseDto;
import com.sparta.hh99_actualproject.dto.MemberResponseDto.ResTagResponseDto;
import com.sparta.hh99_actualproject.dto.MemberResponseDto.RewardResponseDto;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final ResponseTagService responseTagService;

    public boolean signup(MemberRequestDto memberRequestDto) {
        validator.validateSignUpInput(memberRequestDto);

        if (memberRepository.existsByMemberId(memberRequestDto.getMemberId()))
            throw new PrivateException(StatusCode.SIGNUP_MEMBER_ID_DUPLICATE_ERROR);

        Member member = Member.builder()
                .memberId(memberRequestDto.getMemberId())
                .nickname(memberRequestDto.getName())
                .password(passwordEncoder.encode(memberRequestDto.getPassword()))
                .reward(5)
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

    @Transactional
    public MemberResponseDto getMemberProfile(){
        String memberId = SecurityUtil.getCurrentMemberId();

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        MemberResponseDto memberResponseDto = new MemberResponseDto();

        //멤버를 팔로워하는 유저 추출 및 빌드
        //followerMemberList는 나를 팔로워하는 멤버의 수가 들어간다.
        //멤버 테이블에 팔로우에 팔로우 멤버 아이디가 내 아이디인 유저
        List<Follow> getFollowerList = followRepository.findAllByFollowMemberId(memberId);

        ResTagResponseDto resTagResponseDto = responseTagService.findMemberMostResTag(memberId);

        //score에 memberId로 해당 멤버의 score를 찾아온다.
        Score score = null;

        try {
            score = scoreRepository.findByMemberId(memberId).orElseThrow(
                    () -> new PrivateException(StatusCode.NOT_FOUND_SCORE));
        }catch (PrivateException exception){
            score = Score.builder()
                    .score(36.5F)
                    .build();
        }

        memberResponseDto = MemberResponseDto.builder()
                .memberId(memberId)
                .nickname(member.getNickname())
                .color(member.getColor())
                .lovePeriod(member.getLovePeriod())
                .loveType(member.getLoveType())
                .age(member.getAge())
                .dating(member.getDating())
                .score(score.getScore())
                .reward(member.getReward())
                .follower(getFollowerList.size())
                .build();

        if (resTagResponseDto != null && resTagResponseDto.getResTag1() != null){
            memberResponseDto.setResTag1(resTagResponseDto.getResTag1());
        }

        if (resTagResponseDto != null && resTagResponseDto.getResTag2() != null){
            memberResponseDto.setResTag2(resTagResponseDto.getResTag2());
        }

        return memberResponseDto;
    }

    @Transactional
    public List<ChatHistoryResponseDto> getMemberChatHistory() {
        String memberId = SecurityUtil.getCurrentMemberId();

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        //멤버의 채팅내역 추출 및 빌드
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByReqMemberIdAndResMemberId(memberId);

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



            if (member.getMemberId().equals(chatRoom.getReqMemberId())){
                chatHistoryReponseDto.setMyRole("오픈한 상담");
                chatHistoryReponseDto.setNickname(chatRoom.getResNickname());
                chatHistoryReponseDto.setColor(chatRoom.getResMemberColor());
            }else if (member.getMemberId().equals(chatRoom.getResMemberId())){
                chatHistoryReponseDto.setMyRole("참여한 상담");
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
    @Transactional
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
                    .reqMemberNickname(getMessage.getReqUserNickName())
                    .message(getMessage.getMessage())
                    .totalPages(messageList.getTotalPages())
                    .build();
            messageListResponseDtos.add(messageResponseDto);
        }

        return messageListResponseDtos;
    }
    @Transactional
    public List<MemebrInfoFollowResponseDto> getMemberFollow(int page) {
        //멤버의 팔로우 유저 추출 및 빌드
        String memberId = SecurityUtil.getCurrentMemberId();

        PageRequest pageRequest = PageRequest.of(page-1 , 5);

        Page<Follow> getFollowList = followRepository.findAllByMemberMemberIdOrderByCreatedAt(memberId, pageRequest);

        List<MemebrInfoFollowResponseDto> followList = new ArrayList<>();



        for (Follow follow : getFollowList) {
            MemebrInfoFollowResponseDto followResponseDto = MemebrInfoFollowResponseDto.builder()
                    .followMemberId(follow.getFollowMemberId())
                    .createdAt(String.valueOf(follow.getCreatedAt()))
                    .nickname(follow.getNickname())
                    .color(follow.getColor())
                    .totalPages(getFollowList.getTotalPages())
                    .build();
            followList.add(followResponseDto);
        }

        return followList;
    }
    @Transactional
    public BoardResponseDto.AllPostPageResponseDto getMemberBoard(int page) {
        String memberId = SecurityUtil.getCurrentMemberId();

        //멤버 게시글을 모두 가져온다.
        //멤버의 게시글을 리턴형식에 맞게 build할 dto를 생성한다.
        PageRequest pageRequest = PageRequest.of(page-1 , 8);

        Page<SimpleBoardInfoInterface> simpleBoardInfoPages = boardRepository.findAllPostByMemberId(memberId , pageRequest);

        return BoardResponseDto.AllPostPageResponseDto.builder()
                .content(simpleBoardInfoPages.getContent())
                .totalPages(simpleBoardInfoPages.getTotalPages())
                .totalElements(simpleBoardInfoPages.getTotalElements())
                .pageNumber(simpleBoardInfoPages.getPageable().getPageNumber()+1) // Request Page = getPageNumber + 1
                .size(simpleBoardInfoPages.getSize())
                .first(simpleBoardInfoPages.isFirst())
                .last(simpleBoardInfoPages.isLast())
                .empty(simpleBoardInfoPages.isEmpty())
                .build();
    }


    public void checkMemberId(String memberId) {
        if (memberRepository.existsByMemberId(memberId))
            throw new PrivateException(StatusCode.SIGNUP_MEMBER_ID_DUPLICATE_ERROR);
    }

    public void checkNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname))
            throw new PrivateException(StatusCode.SIGNUP_NICKNAME_DUPLICATE_ERROR);
    }


    public RewardResponseDto getReward() {
        String memberId = SecurityUtil.getCurrentMemberId();
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        return RewardResponseDto.builder()
                .reward(member.getReward())
                .build();
    }
}
