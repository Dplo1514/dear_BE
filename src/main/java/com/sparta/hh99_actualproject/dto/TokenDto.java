package com.sparta.hh99_actualproject.dto;

import lombok.*;

@AllArgsConstructor
@Builder
@Data
public class TokenDto {
    private String accessToken;
    private String refreshToken;
}
