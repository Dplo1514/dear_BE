package com.sparta.hh99_actualproject.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper=false)
public class VoteContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteContentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voteBoard_id" ,nullable = false)
    private VoteBoard voteBoard;

    @Column
    private String imageUrl;

    @Column(nullable = false)
    private String imageTitle;
}
