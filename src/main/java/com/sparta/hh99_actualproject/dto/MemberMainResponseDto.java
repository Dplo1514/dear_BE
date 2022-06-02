package com.sparta.hh99_actualproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class MemberMainResponseDto {
    private String nickname;
    private String color;
    private Float score;
    private String resTag1;
}
