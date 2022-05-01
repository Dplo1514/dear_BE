package com.sparta.hh99_actualproject.model;

import com.sparta.hh99_actualproject.dto.VoteBoardInformationRequestDto;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class VoteBoard extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteBoardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id" ,nullable = false)
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @OneToMany(mappedBy = "voteBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteContent> voteContentList;
}
