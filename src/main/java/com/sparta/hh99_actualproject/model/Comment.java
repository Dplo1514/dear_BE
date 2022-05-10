package com.sparta.hh99_actualproject.model;

import com.sparta.hh99_actualproject.dto.CommentRequestDto;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class Comment extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(nullable = false)
    private String content;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id" ,nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id" ,nullable = false)
    private Member member;


    @OneToOne
    @JoinColumn(name = "commentLikes_id" ,nullable = true)
    private CommentLikes commentLikes;

    public void update(CommentRequestDto commentRequestDto) {
        this.content = commentRequestDto.getComment();
    }
    public void likeUpdate(CommentLikes commentLikes) {
        this.commentLikes = commentLikes;
    }
}
