package com.sparta.hh99_actualproject.service;


import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.Member;
import com.sparta.hh99_actualproject.repository.MemberRepository;
import com.sparta.hh99_actualproject.security.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
   private final MemberRepository memberRepository;

   public CustomUserDetailsService(MemberRepository memberRepository) {
      this.memberRepository = memberRepository;
   }

   @Override
   @Transactional
   public UserDetails loadUserByUsername(final String email) {
       Member findMember = memberRepository.findByMemberId(email)
              .orElseThrow(() -> new PrivateException(StatusCode.LOGIN_MEMBER_ID_FAIL));

      return new UserDetailsImpl(findMember);
   }
}
