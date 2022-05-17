package com.sparta.hh99_actualproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceCommentResponseDto {
    private String userId;
    private String comment;
    private String createdAt;
}
