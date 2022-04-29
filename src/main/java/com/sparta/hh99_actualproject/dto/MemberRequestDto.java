package com.sparta.hh99_actualproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberRequestDto {
    private String memberId;

    private String name;

    private String password;

    private String passwordCheck;
}
