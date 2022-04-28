package com.sparta.hh99_actualproject.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
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

    @Column
    private boolean selected;

    @OneToMany(mappedBy = "voteContent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Selection> selectionList;

}
