package com.sparta.hh99_actualproject.dto;

import lombok.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public class CommentDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class CommentResponseDto {

        private Long commentId;

        private Long boardPostId;

        private String member;

        private String comment;

        private boolean likes;

        private String createdAt;

        private Integer totalPages;

        private boolean isFirstOrLast;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class CommentLikesResponseDto {
        private Boolean likes;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class CommentRequestDto {
        private String comment;
    }
}
