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
    @NoArgsConstructor
    @Builder
    @Data
    public static class MessageRequestDto {
        private String resUser;
        private String message;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class MessageResponseDto {
        private String reqUser;
        private String createdAt;
        private String message;
    }


    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class MessageDetailResponseDto {
        private String reqUser;
        private String resUser;
        private String message;
        private String createdAt;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class MemberInfoMessageResponseDto {
        private Long messageId;
        private LocalDateTime createdAt;
        private String reqMemberNickname;
        private String message;
        private Integer totalPages;
        private Pageable nextOrLastPageable;
    }
}
