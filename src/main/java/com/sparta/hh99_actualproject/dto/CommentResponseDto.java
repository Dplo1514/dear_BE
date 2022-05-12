package com.sparta.hh99_actualproject.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Getter
public class CommentResponseDto {

    private Long commentId;

    private Long boardPostId;

    private String member;

    private String comment;

    private boolean likes;

    private String createdAt;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    @Getter
    public static class CommentLikesResponseDto {
        private Boolean likes;
    }
}
