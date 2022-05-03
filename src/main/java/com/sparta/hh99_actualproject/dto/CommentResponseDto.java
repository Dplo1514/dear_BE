package com.sparta.hh99_actualproject.dto;

import com.sparta.hh99_actualproject.model.Board;
import com.sparta.hh99_actualproject.model.Member;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Getter
public class CommentResponseDto {

    private Long commentId;

    private Board board;

    private Member member;

    private String content;

    private boolean liked;
}
