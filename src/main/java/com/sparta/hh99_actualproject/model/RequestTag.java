package com.sparta.hh99_actualproject.model;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class RequestTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestTagId;

    @Column(nullable = false)
    private String memberId;

    @Column(nullable = false)
    private boolean isLike;

    @Column
    private Integer reqTag1Num;

    @Column
    private Integer reqTag2Num;

    @Column
    private Integer reqTag3Num;
}
