package com.sparta.hh99_actualproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EssentialInfoRequestDto {
    private String nickname;

    private String gender;

    private Integer age;

    private String loveType;

    private String lovePeriod;
}