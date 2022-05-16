package com.sparta.hh99_actualproject.dto;


import lombok.*;

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
}
