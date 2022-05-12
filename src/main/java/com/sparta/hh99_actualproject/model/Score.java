package com.sparta.hh99_actualproject.model;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Score {

    @Id
    private String  memberId;

    @Column
    private Float score;

    @Column
    private Integer responseChatCount;

    @Column
    private Integer requestChatCount;

    @Column
    private Integer commentSelectionCount;

    public static Score of(String memberId, float score) {
        return Score.builder()
                .memberId(memberId)
                .score(score)
                .responseChatCount(0)
                .requestChatCount(0)
                .commentSelectionCount(0)
                .build();
    }
}