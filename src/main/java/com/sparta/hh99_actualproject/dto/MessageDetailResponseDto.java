package com.sparta.hh99_actualproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class MessageDetailResponseDto {
    private String reqUserNickName;
    private String resUserNickName;
    private String reqUserId;
    private String resUserId;
    private String message;
    private String createdAt;
}

