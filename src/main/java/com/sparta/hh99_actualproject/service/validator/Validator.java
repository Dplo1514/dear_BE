package com.sparta.hh99_actualproject.service.validator;


import com.sparta.hh99_actualproject.dto.EssentialInfoRequestDto;
import com.sparta.hh99_actualproject.dto.MemberRequestDto;
import com.sparta.hh99_actualproject.dto.VoteBoardRequestDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class Validator {
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

    public void validateEssentialInfoInput(EssentialInfoRequestDto essentialInfoRequestDto){
        if (!isValidNickname(essentialInfoRequestDto.getNickname())) {
            throw new PrivateException(StatusCode.SIGNUP_NICKNAME_FORM_ERROR);
        }
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

    private boolean isValidNickname(String nickname) {
        //nickname : 3~10자 이내 , 영문 ,한글, 숫자
        String pattern = "^[A-Za-z0-9가-힣]{3,10}$";

        return Pattern.matches(pattern, nickname);
    }
}