package com.sparta.hh99_actualproject.dto;

import lombok.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MemberInfoResponseDto {
    private String memberId;
    private String resTag;
    private Integer reward;
    private Float score;
    private List<String> followList;
    private List<String> followerList;
    private List<ChatHistoryReponseDto> chatHistory;
    private List<PostListResponseDto> postList;

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
    public static class PostLikesResponseDto {
        private String memberId;
    }
}
