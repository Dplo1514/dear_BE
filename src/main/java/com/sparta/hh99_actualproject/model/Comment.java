package com.sparta.hh99_actualproject.model;

import lombok.*;

import javax.persistence.*;
@Entity
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper=false)
public class Comment extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id" ,nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id" ,nullable = false)
    private Member member;

    @Column(nullable = false)
    private String content;
}
