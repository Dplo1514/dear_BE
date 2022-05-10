package com.sparta.hh99_actualproject.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
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
    public static class ChatRoomResponseDto {
        private String sessionId;
        private String token;
        private String role;
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
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    @Getter
    public static class ChatRoomResUpdateDto {
        private String resNickname;
        private String resGender;
        private String resLoveType;
        private String resCategory;
        private String resLovePeriod;
    }

}
