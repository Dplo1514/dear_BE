package com.sparta.hh99_actualproject.dto;


import com.sparta.hh99_actualproject.model.SimpleBoardInfoInterface;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;


public class BoardResponseDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class MainResponse{
        private Long boardPostId;
        private LocalDateTime createAt; // 변수타입 한번쯤은 확인 부탁드립니다.
        private String title;
        private String category;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    @Builder
    public static class DetailResponse{
        private Long boardPostId;
        private String memberId;
        private LocalDateTime createAt;
        private String title;
        private String contents;
        private String category;
        private boolean likes;
        private List<String> imgUrl;
        private List<String> likesList;
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
        private String type;
        private Integer comments;
        private Integer likes;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class PostPageResponseDto {
        private Integer pages;
        private List<PostListResponseDto> postListResponseDtoList;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class AllPostPageResponseDto {
        private Page<SimpleBoardInfoInterface> postPageResponseDto;
        private List<Integer> likes;
        private List<Integer> comments;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class MainBoardResponseDto {
        private Long postId;
        private String category;
        private String title;
        private Integer likes;
        private Integer comments;
    }
}
