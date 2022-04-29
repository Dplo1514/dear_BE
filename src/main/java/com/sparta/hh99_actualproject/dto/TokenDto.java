package com.sparta.hh99_actualproject.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {
    private boolean initialLogin;
    private String accessToken;
    private String refreshToken;
}
