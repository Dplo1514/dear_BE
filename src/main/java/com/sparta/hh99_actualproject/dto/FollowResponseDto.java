package com.sparta.hh99_actualproject.dto;

import lombok.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class FollowResponseDto {
    private boolean follow;

    @AllArgsConstructor
    @Builder
    @Data
    public static class MemebrInfoFollowResponseDto {
        private String followMemberId;
        private String nickname;
        private String createdAt;
        private String color;
        private Integer totalPages;
        private Pageable nextOrLastPageable;
    }
}
