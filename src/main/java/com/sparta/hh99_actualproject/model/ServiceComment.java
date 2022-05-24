package com.sparta.hh99_actualproject.model;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ServiceComment extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceCommentId;

    @Column(nullable = false)
    private String memberId;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String serviceComment;

    public static ServiceComment of (String memberId, String nickname,String serviceComment){
        return ServiceComment.builder()
                .memberId(memberId)
                .nickname(nickname)
                .serviceComment(serviceComment)
                .build();
    }
}
