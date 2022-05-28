package com.sparta.hh99_actualproject.model;

import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class Comment extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(nullable = false)
    private String content;

    @Column
    private Boolean isLike;

    @Column
    private String userIp;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id" ,nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id" ,nullable = false)
    private Member member;


}
