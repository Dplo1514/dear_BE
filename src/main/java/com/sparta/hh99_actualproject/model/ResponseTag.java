package com.sparta.hh99_actualproject.model;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ResponseTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long responseTagId;

    @Column(nullable = false)
    private String memberId;

    @Column(nullable = false)
    private boolean isLike;

    @Column
    private Integer resTag1Num;

    @Column
    private Integer resTag2Num;

    @Column
    private Integer resTag3Num;

    @Column
    private Integer resTag4Num;

    @Column
    private Integer resTag5Num;
}
