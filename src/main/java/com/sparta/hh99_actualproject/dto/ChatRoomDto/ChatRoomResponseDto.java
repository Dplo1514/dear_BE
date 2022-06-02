package com.sparta.hh99_actualproject.dto.ChatRoomDto;

import com.sparta.hh99_actualproject.model.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChatRoomResponseDto {
    //req
    private String reqMemberId;
    private String reqTitle;
    private String reqNickname;
    private String reqGender;
    private String reqAge;
    private String reqLoveType;
    private String reqLovePeriod;
    private String reqColor;
    private String reqUserDating;

    //res
    private String resMemberId;
    private String resLovePeriod;
    private String resNickname;
    private String resGender;
    private String resAge;
    private String resLoveType;
    private String resColor;
    private String resUserDating;


    private String category;
    private List<String> imageUrl;


    public void chatRoomResponseInfo(ChatRoom chatRoom, List<String> responseImgUrl){
        this.category = chatRoom.getReqCategory();
        this.reqMemberId = chatRoom.getReqMemberId();
        this.reqAge = chatRoom.getReqAge();
        this.reqGender = chatRoom.getReqGender();
        this.reqLovePeriod = chatRoom.getReqLovePeriod();
        this.reqLoveType = chatRoom.getReqLoveType();
        this.reqNickname = chatRoom.getReqNickname();
        this.reqTitle = chatRoom.getReqTitle();
        this.reqColor = chatRoom.getReqMemberColor();
        this.reqUserDating = chatRoom.getReqMemberDating();
        this.resMemberId = chatRoom.getResMemberId();
        this.resAge = chatRoom.getResAge();
        this.resGender = chatRoom.getResGender();
        this.resLovePeriod = chatRoom.getResLovePeriod();
        this.resLoveType = chatRoom.getResLoveType();
        this.resNickname = chatRoom.getResNickname();
        this.resColor = chatRoom.getResMemberColor();
        this.resUserDating = chatRoom.getResMemberDating();
        this.imageUrl = responseImgUrl;
    }
}
