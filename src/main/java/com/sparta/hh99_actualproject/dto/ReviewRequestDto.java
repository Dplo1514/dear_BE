package com.sparta.hh99_actualproject.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ReviewRequestDto {

    private boolean requestReview;

    private String oppositeMemberId;

    private boolean like;

    List<Boolean> tagSelectList;

    private String serviceComment;
}
