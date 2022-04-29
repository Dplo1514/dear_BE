package com.sparta.hh99_actualproject.jwt;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

   @Override
   public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
      //필요한 권한이 없이 접근하려 할때 403
//      response.sendError(HttpServletResponse.SC_FORBIDDEN);

      System.out.println("해당 메서드에 접근하기 위한 권한이 부족합니다. 본인의 권한을 다시 확인해주세요.");
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      PrintWriter writer = response.getWriter();
      writer.println("HTTP Status 403 / ErrCode : 999 , Msg : Plz check User's Authority. Plz Check your Authority to your Manager");
   }
}
