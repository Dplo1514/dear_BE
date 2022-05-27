package com.sparta.hh99_actualproject.util;

import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {

   private SecurityUtil() {
   }

   public static String getCurrentMemberId() {
      final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (authentication == null) {
         throw new PrivateException(StatusCode.NOT_FOUND_AUTHORIZATION_IN_SECURITY_CONTEXT);
      }

      String memberId = null;
      String memberName = null;
      if (authentication.getPrincipal() instanceof UserDetails) {
         UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
         memberId = springSecurityUser.getUsername();
      } else if (authentication.getPrincipal() instanceof String) {
         memberId = (String) authentication.getPrincipal();
      }

      return memberId;
   }
}