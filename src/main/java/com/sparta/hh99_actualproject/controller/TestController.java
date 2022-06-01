package com.sparta.hh99_actualproject.controller;

import com.sparta.hh99_actualproject.exception.PrivateResponseBody;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final Environment env;

    @GetMapping("/api/test/getERR")
    public void getPosts(){
        throw new PrivateException(StatusCode.NOT_FOUND_POST);
    }

    @GetMapping("/api/test/ok")
    public ResponseEntity<PrivateResponseBody> getPosts_ok(){
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK),HttpStatus.OK);
    }

    @GetMapping("/api/logintest")
    public ResponseEntity<PrivateResponseBody> getLoginTest(){
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,"로그인 테스트 성공"), HttpStatus.OK);
    }

    //profile 조회
    @GetMapping(value="/api/test/profile")
    public String getProfile(){
        return Arrays.stream(env.getActiveProfiles())
                .findFirst()
                .orElse("");
    }

}
