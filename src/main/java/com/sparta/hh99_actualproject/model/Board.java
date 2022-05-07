package com.sparta.hh99_actualproject.model;

import com.sparta.hh99_actualproject.dto.BoardRequestDto;
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
public class Board extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardPostId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id" ,nullable = false)
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contents;

    @Column
    private String category;

    @Column
    private boolean liked;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Img> imgList;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Likes> likesList;

    //202205071820 변경
    public void update(BoardRequestDto.SaveRequest boardRequestDto){
        this.title = boardRequestDto.getTitle();
        this.category = boardRequestDto.getCategory();
        this.contents = boardRequestDto.getContents();
    }

}