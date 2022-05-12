package com.sparta.hh99_actualproject.service.validator;


import com.sparta.hh99_actualproject.dto.*;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.Board;
import com.sparta.hh99_actualproject.model.Comment;
import com.sparta.hh99_actualproject.repository.MemberRepository;
import com.sparta.hh99_actualproject.util.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Component
@AllArgsConstructor
public class Validator {

    private final MemberRepository memberRepository;

    public String validateMyMemberId() {
        //Token MemberId 확인하기
        if(!memberRepository.existsByMemberId(SecurityUtil.getCurrentMemberId()))
            throw new PrivateException(StatusCode.NOT_FOUND_MEMBER);
        return SecurityUtil.getCurrentMemberId();
    }
    public void validateSignUpInput(MemberRequestDto memberRequestDto) {
        if(hasNullDtoField(memberRequestDto)){
            throw new PrivateException(StatusCode.NULL_INPUT_ERROR);
        }

        if (!isValidMemberId(memberRequestDto.getMemberId())) {
            throw new PrivateException(StatusCode.SIGNUP_MEMBER_ID_FORM_ERROR);
        }

        if (!isValidPasswordCheck(memberRequestDto.getPassword(),memberRequestDto.getPasswordCheck())) {
            throw new PrivateException(StatusCode.SIGNUP_PASSWORD_CHECK_ERROR);
        }

        if (!isValidPassword(memberRequestDto.getPassword() , memberRequestDto.getMemberId())) {
            throw new PrivateException(StatusCode.SIGNUP_PASSWORD_FORM_ERROR);
        }
    }

    public boolean hasNullDtoField(VoteBoardRequestDto requestDto) {
        return requestDto.getTitle() == null ||
                requestDto.getContents() == null ||
                requestDto.getImgLeftTitle() == null ||
                requestDto.getImgRightTitle() == null ||
                requestDto.getTitle().trim().equals("") ||
                requestDto.getContents().trim().equals("") ||
                requestDto.getImgLeftTitle().trim().equals("") ||
                requestDto.getImgRightTitle().trim().equals("");
    }

    private boolean hasNullDtoField(MemberRequestDto memberRequestDto) {
        return  memberRequestDto.getMemberId() == null ||
                memberRequestDto.getPassword() == null ||
                memberRequestDto.getPasswordCheck() == null;
    }

    private boolean isValidPassword(String password, String memberId) {
        // 6자 ~ 12자 , 영문 , 숫자 ,  memberId 같은 값 포함 x ,
        String pattern = "^[A-Za-z0-9]{6,12}$";

        return Pattern.matches(pattern, password) && !password.contains(memberId);
    }

    private boolean isValidPasswordCheck(String password, String passwordCheck) {
        //password 는 passwordCheck 와 동일해야 한다.
        return password.equals(passwordCheck);
    }

    private boolean isValidMemberId(String memberId) {
        //memberId : 3~10자 이내 , 영문 , 숫자
        String pattern = "^[A-Za-z0-9]{3,10}$";

        return Pattern.matches(pattern, memberId);
    }

    public boolean isValidNickname(String nickname) {
        //nickname : 3~10자 이내 , 영문 ,한글, 숫자
        String pattern = "^[A-Za-z0-9가-힣]{3,10}$";

        return Pattern.matches(pattern, nickname);
    }

    public void hasNullCheckComment(CommentRequestDto commentRequestDto) {
        if (!StringUtils.hasText(commentRequestDto.getComment()) || commentRequestDto.getComment().trim().equals("")){
            throw new PrivateException(StatusCode.NULL_INPUT_ERROR);
        }
    }


    public void hasValidCheckAuthorityComment(String memberId, Comment comment) {
        if (!comment.getMember().getMemberId().equals(memberId)){
            throw new PrivateException(StatusCode.WRONG_ACCESS_COMMENT_UPDATE);
        }
    }

    public void hasValidCheckEffectiveComment(Long boardId, Comment comment) {
        if (!comment.getBoard().getBoardPostId().equals(boardId)){
            throw new PrivateException(StatusCode.NOT_FOUND_POST);
        }
    }

    public void hasValidCheckAuthorityCommentLike(String memberId, Board board) {
        if (!board.getMember().getMemberId().equals(memberId)){
            throw new PrivateException(StatusCode.WRONG_ACCESS_COMMENTLIKES);
        }
    }

    public boolean isValidSelectionNum(Integer selectionNum) {
        return selectionNum != 1 && selectionNum != 2;
    }

    public String validateOppositeMemberId(ReviewRequestDto reqReviewRequestDto) {
        //반대편 MemberId 확인하기
        if(!memberRepository.existsByMemberId(reqReviewRequestDto.getOppositeMemberId()))
            throw new PrivateException(StatusCode.NOT_FOUND_MEMBER);
        return reqReviewRequestDto.getOppositeMemberId();
    }

    public void hasNullChekckReqChat(ChatRoomDto.ChatRoomReqRequestDto requestDto) {
        if (requestDto.getReqTitle() == null || requestDto.getReqCategory() == null || requestDto.getReqGender() == null){
            new PrivateException(StatusCode.NULL_INPUT_CHAT_REQUEST);
        }
    }

    public void hasNullChekckResChat(ChatRoomDto.ChatRoomResRequestDto requestDto) {
        if (requestDto.getResCategory() == null){
            new PrivateException(StatusCode.NULL_INPUT_CHAT_RESPONSE);
        }
    }
}