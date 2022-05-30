//package com.sparta.hh99_actualproject.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@RequiredArgsConstructor
//@Service
//public class RedisService {
//
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    public void redisString() {
//        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
//        operations.set("test", "test");
//        String redis = (String)operations.get("test");
//        log.info(redis);
//    }
//}
//
