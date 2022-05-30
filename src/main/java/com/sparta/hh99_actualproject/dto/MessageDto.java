package com.sparta.hh99_actualproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;


public class MessageDto {
    @AllArgsConstructor
    @Builder
    @Data
    public static class MessageRequestDto {
        private String resUserId;
        private String message;
    }


    @AllArgsConstructor
    @Builder
    @Data
    public static class MessageDetailResponseDto {
        private String reqUserNickName;
        private String resUserNickName;
        private String reqUserId;
        private String resUserId;
        private String message;
        private String createdAt;
    }

    @AllArgsConstructor
    @Builder
    @Data
    public static class MemberInfoMessageResponseDto {
        private Long messageId;
        private LocalDateTime createdAt;
        private String reqMemberNickname;
        private String message;
        private Integer totalPages;
    }
}
