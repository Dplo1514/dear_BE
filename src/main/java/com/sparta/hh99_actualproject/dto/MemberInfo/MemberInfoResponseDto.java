package com.sparta.hh99_actualproject.dto.MemberInfo;

import lombok.*;


@AllArgsConstructor
@Setter
@Builder
@Getter
public class MemberInfoResponseDto {
    private String memberId;
    private String nickname;
    private String gender;
    private String dating;
    private String age;
    private String loveType;
    private String lovePeriod;
    private String color;
    private Float reward;
    private Float score;
    private Integer follower;
    private String resTag1;
    private String resTag2;
}
