package com.sparta.hh99_actualproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class VoteBoardResponseDto {
    private Long postId;
    private String memberId;
    private List<VoteContentResponseDto> vote;
    private LocalDateTime createdAt;
    private String title;
    private String contents;
}
