package com.sparta.hh99_actualproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MainBoardResponseDto {
    private Long postId;
    private String category;
    private String title;
    private Integer likes;
    private Integer comments;
}
