package com.sparta.hh99_actualproject.dto;

import jdk.vm.ci.meta.Local;
import lombok.*;
import org.joda.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public class ChatRoomDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    @Getter
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
    @Getter
    public static class ChatRoomResRequestDto {
        private String resCategory;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    @Getter
    public static class ChatRoomMatchResponseDto {
        private String sessionId;
        private String token;
        private String role;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    @Getter
    public static class ChatRoomResponseDto {
        //res
        private String reqTitle;
        private String reqNickName;
        private String reqGender;
        private String reqAge;
        private String reqLoveType;
        private String reqLovePeriod;

        //req
        private String resLovePeriod;
        private String resNickName;
        private String resGender;
        private String resAge;
        private String resLoveType;

        private LocalDateTime createdAt;

        private List<String> imageUrl;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    @Getter
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
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    @Getter
    public static class ChatRoomResUpdateDto {
        private String resCategory;
        private String resNickname;
        private String resGender;
        private String resLoveType;
        private String resAge;
        private String resLovePeriod;
        private String matchTime;
    }

}
