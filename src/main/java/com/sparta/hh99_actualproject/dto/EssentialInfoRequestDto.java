package com.sparta.hh99_actualproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EssentialInfoRequestDto {
    private String nickname;

    private String color;

    private String gender;

    private String age;

    private String loveType;

    private String lovePeriod;
}
