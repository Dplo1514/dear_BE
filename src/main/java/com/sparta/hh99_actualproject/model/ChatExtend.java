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
public class ChatExtend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatExtendId;

    @Column
    private String reqMemberId;

    @Column
    private String resMemberId;

    @Column(columnDefinition = "int default 0")
    private int extendCount;

    @OneToOne(mappedBy = "chatExtend")
    private ChatRoom chatRoom;


}
