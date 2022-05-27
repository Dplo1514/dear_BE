package com.sparta.hh99_actualproject.config;

import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.Member;
import com.sparta.hh99_actualproject.repository.MemberRepository;
import com.sparta.hh99_actualproject.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


@Aspect
@Component
@RequiredArgsConstructor
public class AopConfig {

    private final MemberRepository memberRepository;


    @Before("execution(* com.sparta.hh99_actualproject.service.ChatService.*(..))")
    public void checkUser(){
        String memberId = SecurityUtil.getCurrentMemberId();
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        if (member.getNickname() == null || member.getDating() == null){
            throw new PrivateException(StatusCode.LOGIN_MEMBER_REQUIRED_INFORMATION_FAIL);
        }
    }

//    @Before("execution(* com.sparta.hh99_actualproject.service.ChatService.*(..))")
//    public void wrongAccessMatch(HttpServletRequest httpServletRequest){
//
//    }
}

