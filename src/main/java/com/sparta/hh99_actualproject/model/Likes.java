package com.sparta.hh99_actualproject.model;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Builder
@AllArgsConstructor
public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likesId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id" ,nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id" ,nullable = false)
    private Board board;
}
