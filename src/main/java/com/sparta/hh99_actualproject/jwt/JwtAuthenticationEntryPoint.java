package com.sparta.hh99_actualproject.jwt;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

   @Override
   public void commence(HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
      // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
//      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);

      //Login 요청에서 Unauthorized 가 뜨면 비밀번호 가 틀린 경우
      if (request.getRequestURI().startsWith("/user/login")) {
         System.out.println("잘못된 비밀번호를 입력하셨습니다.");
         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
         PrintWriter writer = response.getWriter();
         writer.println("HTTP Status 401 / ErrCode : 111 , Msg : Plz check User's Password. Password is not Matching with DB Password");
      }
      // Login 요청 제외하고 Unauthorized 가 뜨면 Token 이 없거나 잘못된 토큰을 가지고 있는 경우
      else {
         System.out.println("Token 이 없습니다. 다시 확인해주세요.");
         response.setStatus(HttpServletResponse.SC_NOT_FOUND);
         PrintWriter writer = response.getWriter();
         writer.println("HTTP Status 404 / ErrCode : 500 , Msg : Plz check Request's Header about Authorization. it is not present");
      }
   }
}