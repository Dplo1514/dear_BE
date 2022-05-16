package com.sparta.hh99_actualproject.dto;

import com.sparta.hh99_actualproject.model.Follow;
import lombok.*;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MemberInfoResponseDto {
    private String memberId;
    private String nickname;
    private String dating;
    private String age;
    private String loveType;
    private String lovePeriod;
    private String color;
    private Integer reward;
    private Float score;
    private Integer follower;
    private ResTagResponseDto resTags;




    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class ChatHistoryReponseDto {
        private String reqComment;
        private String reqCategory;
        private String createdAt;
        private String chatTime;
        private String myRole;
        private String nickname;
        private String color;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class PostListResponseDto {
        private Long postId;
        private String createdAt;
        private String title;
        private String category;
        private Integer comments;
        private Integer likes;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class MessageResponseDto {
        private LocalDateTime createdAt;
        private String reqMemberNickname;
        private String message;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class ResTagResponseDto {
        private String resTag1;
        private String resTag2;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class FollowResponseDto {
        private String nickname;
        private LocalDateTime createdAt;
        private String color;
    }
}
