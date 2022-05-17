package com.sparta.hh99_actualproject.service.validator;


import com.sparta.hh99_actualproject.dto.*;
import com.sparta.hh99_actualproject.dto.CommentDto.CommentRequestDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.Board;
import com.sparta.hh99_actualproject.model.ChatRoom;
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

    public void validateMemberInfoInput(EssentialInfoRequestDto essentialInfoRequestDto) {
        //null Check
        if (hasNullDtoField(essentialInfoRequestDto)){
            throw new PrivateException(StatusCode.NULL_INPUT_ERROR);
        }

        if(!isValidNickname(essentialInfoRequestDto.getNickname()))
            throw new PrivateException(StatusCode.SIGNUP_NICKNAME_FORM_ERROR);
    }

    public boolean hasNullDtoField(EssentialInfoRequestDto essentialInfoRequestDto) {
        return essentialInfoRequestDto.getNickname() == null ||
                essentialInfoRequestDto.getColor() == null ||
                essentialInfoRequestDto.getGender() == null ||
                essentialInfoRequestDto.getAge() == null ||
                essentialInfoRequestDto.getLoveType() == null ||
                essentialInfoRequestDto.getLovePeriod() == null ||

                essentialInfoRequestDto.getNickname().trim().equals("") ||
                essentialInfoRequestDto.getColor().trim().equals("") ||
                essentialInfoRequestDto.getGender().trim().equals("") ||
                essentialInfoRequestDto.getAge().trim().equals("")||
                essentialInfoRequestDto.getLoveType().trim().equals("")||
                essentialInfoRequestDto.getLovePeriod().trim().equals("");
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

    //null이면 false가 리턴된다.
    public void hasNullCheckComment(CommentRequestDto commentRequestDto) {
        if (!StringUtils.hasText(commentRequestDto.getComment()) || commentRequestDto.getComment().trim().equals("")){
            throw new PrivateException(StatusCode.NULL_INPUT_ERROR);
        }
    }

    //댓글 업데이트 권한체크
    public void hasValidCheckAuthorityComment(String memberId, Comment comment) {
        if (!comment.getMember().getMemberId().equals(memberId)){
            throw new PrivateException(StatusCode.WRONG_ACCESS_COMMENT_UPDATE);
        }
    }

    //댓글이 달려있는 게시글의 존재여부 체크
    public void hasValidCheckEffectiveComment(Long boardId, Comment comment) {
        if (!comment.getBoard().getBoardPostId().equals(boardId)){
            throw new PrivateException(StatusCode.NOT_FOUND_POST);
        }
    }

    //댓글 채택 후 수정 막기
    public void hasValidCheckCommentIsAccepted(Comment comment) {
        if (comment.getIsLike()){
            throw new PrivateException(StatusCode.WRONG_ACCESS_COMMENT_UPDATE_LIKE);
        }
    }

    //댓글 좋아요 셀프 체크
    public void isValidCheckCommentSelfChoose(String memberId, Comment comment) {
        if (!comment.getMember().getMemberId().equals(memberId)) {
            throw new PrivateException(StatusCode.WRONG_ACCESS_CHECK_SELF_COMMENTLIKES);
        }
    }

    //댓글 좋아요 권한 체크
    public void hasValidCheckAuthorityCommentLike(String memberId, Comment comment) {
        if (!comment.getBoard().getMember().getMemberId().equals(memberId)){
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

    public void hasValidCheckExtend(ChatRoom chatRoom) {
        if (chatRoom.getChatExtend().getExtendCount() >= 6) {
            throw new PrivateException(StatusCode.WRONG_REQUEST_CHAT_ROOM);
        }
    }

    public void hasNullCheckMessage(MessageDto.MessageRequestDto messageRequestDto) {
        if (!StringUtils.hasText(messageRequestDto.getMessage()) || messageRequestDto.getMessage().trim().equals("")
                ||!StringUtils.hasText(messageRequestDto.getResUser()) || messageRequestDto.getResUser().trim().equals("")){
            throw new PrivateException(StatusCode.NULL_INPUT_MESSAGE_ERROR);
        }
    }
}