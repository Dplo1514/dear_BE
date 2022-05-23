package com.sparta.hh99_actualproject.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class Notification extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(nullable = false)
    private String memberId;

    @Column
    private String oppositeMemberColor;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private NotiTypeEnum notiType;

    @Column
    private Long notiPostId;

    @Column
    private String notiContent;

    @Column
    private boolean isRead;

    public void setRead(boolean value) {
        this.isRead = value;
    }
}
