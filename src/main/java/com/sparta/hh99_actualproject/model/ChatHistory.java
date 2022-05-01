package com.sparta.hh99_actualproject.model;

import lombok.*;

import javax.persistence.*;
@Entity
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper=false)
public class ChatHistory extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id" ,nullable = false)
    private Member member;

    @Column
    private String reqComment;

    @Column
    private String reqCategory;

    @Column
    private String resCategory;

    @Column
    private Integer chatTime;

    @Column(nullable = false)
    private String myRole;
}
