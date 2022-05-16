package com.sparta.hh99_actualproject.dto;

import io.openvidu.java.client.Session;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public class ChatRoomDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class ChatRoomReqRequestDto {
        private List<MultipartFile> imgList;
        private String reqTitle;
        private String reqCategory;
        private String reqGender;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class ChatRoomResRequestDto {
        private String resCategory;
        private String resGender;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class ChatRoomMatchResponseDto {
        private String sessionId;
        private String token;
        private String role;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class ChatRoomResponseDto {

        //req
        private String reqTitle;
        private String reqNickname;
        private String reqGender;
        private String reqAge;
        private String reqLoveType;
        private String reqLovePeriod;
        private String reqColor;

        //res
        private String resLovePeriod;
        private String resNickname;
        private String resGender;
        private String resAge;
        private String resLoveType;
        private String resColor;


        private String category;
        private List<String> imageUrl;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class ChatRoomReqUpdateDto {
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
        private String matchTime;
        private String reqUserColor;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class ChatRoomResUpdateDto {
        private String resCategory;
        private String resNickname;
        private String resGender;
        private String resLoveType;
        private String resAge;
        private String resLovePeriod;
        private String matchTime;
        private String resUserColor;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class ChatHistoryResponseDto {
        private String reqComment;
        private String reqCategory;
        private String createdAt;
        private String chatTime;
        private String myRole;
        private String nickname;
        private String color;
    }

}
