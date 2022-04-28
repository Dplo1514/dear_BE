package com.sparta.hh99_actualproject.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class Selection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long selectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voteContent_id" ,nullable = false)
    private VoteContent voteContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id" ,nullable = false)
    private Member member;
}
