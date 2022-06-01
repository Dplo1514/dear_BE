package com.sparta.hh99_actualproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void redisTest(String redisTest) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        operations.set( redisTest , "RedisTest입니다.");
    }
}
