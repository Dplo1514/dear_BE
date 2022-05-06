package com.sparta.hh99_actualproject.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class BoardRequestDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    @Builder
    public static class SaveRequest{
        private List<MultipartFile> files;
        private String title;
        private String category;
        private String contents;
    }
/*
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    @Builder

public static class UpdateRequest{
        private List<MultipartFile> files;
        private String title;
        private String category;
        private String contents;
    }
*/


}
