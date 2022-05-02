package com.sparta.hh99_actualproject.model;

import com.sparta.hh99_actualproject.dto.EssentialInfoRequestDto;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper=false)
public class Member {
    @Id
    private String memberId;

    @Column
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column
    private String gender;

    @Column
    private Integer age;

    @Column
    private String loveType;

    @Column
    private String lovePeriod;

    @Column
    private String kakaoUserId;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatHistory> chatHistoryList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messageList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceComment> serviceCommentList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> boardList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Likes> likesList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteBoard> voteBoardList;

    public void updateMemberEssentialInfo(EssentialInfoRequestDto essentialInfoRequestDto) {
        this.nickname = essentialInfoRequestDto.getNickname();

        this.gender = essentialInfoRequestDto.getGender();

        this.age = essentialInfoRequestDto.getAge();

        this.loveType = essentialInfoRequestDto.getLoveType();

        this.lovePeriod = essentialInfoRequestDto.getLovePeriod();
    }
}
