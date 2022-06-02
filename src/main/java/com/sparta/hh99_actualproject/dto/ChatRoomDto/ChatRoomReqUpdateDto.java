package com.sparta.hh99_actualproject.dto.ChatRoomDto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChatRoomReqUpdateDto {
    private String reqMemberId;
    private String imgUrl1;
    private String imgUrl2;
    private String imgUrl3;
    private String reqNickname;
    private String reqTitle;
    private String reqGender;
    private String reqCategory;
    private String reqAge;
    private String reqLoveType;
    private String reqLovePeriod;
    private String reqUserColor;
    private String reqUserDating;
    private String reqUserIp;
    private String matchTime;
}
