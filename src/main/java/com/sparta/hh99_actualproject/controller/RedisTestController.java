package com.sparta.hh99_actualproject.controller;

import com.sparta.hh99_actualproject.exception.PrivateResponseBody;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.service.LocalRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/redis")
public class RedisTestController {

    private final LocalRedisService localRedisService;

    //게시글 전체조회
    @GetMapping("/test")
    public ResponseEntity<PrivateResponseBody> redistest(String redisTest) {
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK, localRedisService.redisTest(redisTest)), HttpStatus.OK);

    }
}
