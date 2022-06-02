package com.sparta.hh99_actualproject.dto;

import lombok.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Builder
@Getter
public class CommentResponseDto {

    private Long commentId;

    private Long boardPostId;

    private String member;

    private String comment;

    private boolean likes;

    private String createdAt;

    private Integer totalPages;

    private Integer totalComments;

}


