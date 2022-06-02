package com.sparta.hh99_actualproject.dto.ChatRoomDto;


import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class ChatRoomResUpdateDto {
    private String resMemberId;
    private String resCategory;
    private String resNickname;
    private String resGender;
    private String resLoveType;
    private String resAge;
    private String resLovePeriod;
    private String matchTime;
    private String resUserColor;
    private String resUserDating;
    private String resUserIp;
}
