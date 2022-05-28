package com.sparta.hh99_actualproject.service.validator;


import com.sparta.hh99_actualproject.dto.*;
import com.sparta.hh99_actualproject.dto.CommentDto.CommentRequestDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.ChatRoom;
import com.sparta.hh99_actualproject.model.Comment;
import com.sparta.hh99_actualproject.model.Member;
import com.sparta.hh99_actualproject.repository.MemberRepository;
import com.sparta.hh99_actualproject.service.ClientIpService;
import com.sparta.hh99_actualproject.util.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

@Component
@AllArgsConstructor
public class Validator {

    private final MemberRepository memberRepository;

    private final ClientIpService clientIpService;

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

        if(essentialInfoRequestDto.getDating().equals("커플")){
            if (essentialInfoRequestDto.getLoveType() == null || essentialInfoRequestDto.getLovePeriod() == null) {
                throw new PrivateException(StatusCode.NULL_INPUT_ERROR);
            }
        }

        if(!isValidNickname(essentialInfoRequestDto.getNickname()))
            throw new PrivateException(StatusCode.SIGNUP_NICKNAME_FORM_ERROR);
    }

    public boolean hasNullDtoField(EssentialInfoRequestDto essentialInfoRequestDto) {
        return essentialInfoRequestDto.getNickname() == null ||
                essentialInfoRequestDto.getColor() == null ||
                essentialInfoRequestDto.getGender() == null ||
                essentialInfoRequestDto.getAge() == null ||
                essentialInfoRequestDto.getDating() == null ||

                essentialInfoRequestDto.getNickname().trim().equals("") ||
                essentialInfoRequestDto.getColor().trim().equals("") ||
                essentialInfoRequestDto.getGender().trim().equals("") ||
                essentialInfoRequestDto.getAge().trim().equals("")||
                essentialInfoRequestDto.getDating().trim().equals("");
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

    //댓글 좋아요 권한 체크
    public void hasValidCheckAuthorityCommentLike(String memberId, Comment comment) {
        if (!comment.getBoard().getMember().getMemberId().equals(memberId)){
            throw new PrivateException(StatusCode.WRONG_ACCESS_COMMENTLIKES);
        }
    }

    //댓글 채택 셀프 체크
    public void isValidCheckCommentSelfChoose(String memberId, Comment comment) {
        if (comment.getMember().getMemberId().equals(memberId)) {
            throw new PrivateException(StatusCode.WRONG_ACCESS_CHECK_SELF_COMMENTLIKES);
        }
    }

    //댓글 채택 셀프체크
    public void isValidCheckCommentSelfChooseIp(Comment comment , String userIp) {
        if (comment.getUserIp().equals(userIp)){
            throw new PrivateException(StatusCode.WRONG_ACCESS_CHECK_SELF_COMMENTLIKES_IP);
        }
    }

    public boolean isNotValidSelectionNum(Integer selectionNum) {
        return selectionNum != 1 && selectionNum != 2;
    }

    public String validateOppositeMemberId(ReviewRequestDto reqReviewRequestDto) {
        //반대편 MemberId 확인하기
        if(!memberRepository.existsByMemberId(reqReviewRequestDto.getOppositeMemberId()))
            throw new PrivateException(StatusCode.NOT_FOUND_MEMBER);
        return reqReviewRequestDto.getOppositeMemberId();
    }

    public void hasNullChekckReqChat(ChatRoomDto.ChatRoomReqRequestDto requestDto) {
        if (requestDto.getReqTitle() == null || requestDto.getReqTitle().trim().equals("") ||
                requestDto.getReqCategory() == null || requestDto.getReqCategory().trim().equals("") ||
                requestDto.getReqGender() == null || requestDto.getReqGender().trim().equals("")){
            new PrivateException(StatusCode.NULL_INPUT_CHAT_REQUEST);
        }
    }

    public void hasNullChekckResChat(ChatRoomDto.ChatRoomResRequestDto requestDto) {
        if (requestDto.getResCategory() == null || requestDto.getResCategory().trim().equals("")
                || requestDto.getResGender() == null || requestDto.getResGender().trim().equals("")){
            new PrivateException(StatusCode.NULL_INPUT_CHAT_RESPONSE);
        }
    }

    public void hasWrongCheckChatCategory(String category) {
        ArrayList<String> matchCategory = new ArrayList<>(Arrays.asList("솔로", "썸", "짝사랑", "연애", "이별", "기타"));
        if (!matchCategory.contains(category)){
            throw new PrivateException(StatusCode.WRONG_ACCESS_CHAT_MATCH_CATEGORY);
        }
    }

    //동일 유저의 채팅 매치 방지
    public void hasSameCheckReqMember(Member member, ChatRoom chatRoom) {
        if (chatRoom.getResMemberId().equals(member.getMemberId())) {
            throw new PrivateException(StatusCode.WRONG_ACCESS_CHAT_MATCH_SAME_USER);
        }
    }

    public void hasSameCheckResMember(Member member, ChatRoom chatRoom) {
        if (chatRoom.getReqMemberId().equals(member.getMemberId())) {
            throw new PrivateException(StatusCode.WRONG_ACCESS_CHAT_MATCH_SAME_USER);
        }
    }

    public void hasSameIpCheckReqMember(String userIp, ChatRoom chatRoom) {
        if (userIp.equals(chatRoom.getResUserIp())){
            new PrivateException(StatusCode.WRONG_ACCESS_CHAT_MATCH_SAME_IP);
        }
    }

    public void hasSameIpCheckResMember(String userIp, ChatRoom chatRoom) {
        if (userIp.equals(chatRoom.getReqUserIp())){
            new PrivateException(StatusCode.WRONG_ACCESS_CHAT_MATCH_SAME_IP);
        }
    }

    public void isRewardCheckMember(Member member) {
        if (member.getReward() == null || member.getReward() <= 1) {
            throw new PrivateException(StatusCode.WRONG_ACCESS_CHAT_REWARD);
        }
    }

    public void hasValidCheckExtend(ChatRoom chatRoom) {
        if (chatRoom.getChatExtend().getExtendCount() >= 6) {
            throw new PrivateException(StatusCode.WRONG_REQUEST_CHAT_ROOM);
        }
    }


    public void hasNullCheckMessage(MessageDto.MessageRequestDto messageRequestDto) {
        if (!StringUtils.hasText(messageRequestDto.getMessage()) || messageRequestDto.getMessage().trim().equals("")
                ||!StringUtils.hasText(messageRequestDto.getResUserNickName()) || messageRequestDto.getResUserNickName().trim().equals("")){
            throw new PrivateException(StatusCode.NULL_INPUT_MESSAGE_ERROR);
        }
    }
}