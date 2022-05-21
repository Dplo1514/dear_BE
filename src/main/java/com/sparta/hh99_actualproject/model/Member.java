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
    private String color;

    @Column
    private String gender;

    @Column
    private String age;

    @Column
    private String dating;

    @Column
    private String loveType;

    @Column
    private String lovePeriod;

    @Column
    private String kakaoUserId;

    @Column(columnDefinition = "integer default 5")
    private Integer reward;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messageList;
    
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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoom> chatRoomList;


    public void updateMemberEssentialInfo(EssentialInfoRequestDto essentialInfoRequestDto) {
        this.nickname = essentialInfoRequestDto.getNickname();

        this.color = essentialInfoRequestDto.getColor();

        this.gender = essentialInfoRequestDto.getGender();

        this.age = essentialInfoRequestDto.getAge();

        this.loveType = essentialInfoRequestDto.getLoveType();

        this.lovePeriod = essentialInfoRequestDto.getLovePeriod();

        this.dating = essentialInfoRequestDto.getDating();
    }

}
