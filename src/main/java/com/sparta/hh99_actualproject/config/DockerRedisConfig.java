//package com.sparta.hh99_actualproject.config;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisServer;
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//
////로컬 환경일경우 내장 레디스가 실행됩니다.
//@Configuration
//@RequiredArgsConstructor
//public class DockerRedisConfig {
//    @Value("${localhost:6379}")
//    private int redisPort;
//
//    private final RedisServer redisServer;
//
//    @PostConstruct
//    public void redisServer() {
//        redisServer = new RedisServer(redisPort);
//        redisServer.start();
//        redisServer.
//    }
//
//    @PreDestroy
//    public void stopRedis() {
//        if (redisServer != null) {
//            redisServer.stop();
//        }
//    }
//}
//
//}
