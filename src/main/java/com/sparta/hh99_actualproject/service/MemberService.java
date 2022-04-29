package com.sparta.hh99_actualproject.service;


import com.sparta.hh99_actualproject.dto.CheckRequestDto;
import com.sparta.hh99_actualproject.dto.EssentialInfoRequestDto;
import com.sparta.hh99_actualproject.dto.MemberRequestDto;
import com.sparta.hh99_actualproject.dto.TokenDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.jwt.TokenProvider;
import com.sparta.hh99_actualproject.model.Member;
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

@Service
@AllArgsConstructor
public class MemberService {
    private MemberRepository memberRepository;
    private Validator validator;
    private PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public boolean signup(MemberRequestDto memberRequestDto) {
        validator.validateSignUpInput(memberRequestDto);

        if(memberRepository.existsByMemberId(memberRequestDto.getMemberId()))
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
        boolean isInitialLogin = (findedMember.getNickname() == null); //findedMember.getNickname()가 Null 이면 최초 로그인

        //TODO : refreshToken 구현 필요
        String accessToken = tokenProvider.createAccessToken(authentication.getName(),findedMember.getNickname());

        tokenDto = TokenDto.builder()
                .initialLogin(isInitialLogin)
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
        String accessToken = tokenProvider.createAccessToken(findedMember.getMemberId(),findedMember.getNickname());

        tokenDto = TokenDto.builder()
                .initialLogin(false)
                .accessToken(accessToken)
                .build();

        return tokenDto;
    }

    public void checkMemberId(String  memberId) {
        if(memberRepository.existsByMemberId(memberId))
            throw new PrivateException(StatusCode.SIGNUP_MEMBER_ID_DUPLICATE_ERROR);
    }

    public void checkNickname(String nickname) {
        if(memberRepository.existsByNickname(nickname))
            throw new PrivateException(StatusCode.SIGNUP_NICKNAME_DUPLICATE_ERROR);
    }
}
