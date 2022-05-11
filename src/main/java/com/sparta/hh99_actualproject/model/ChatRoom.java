package com.sparta.hh99_actualproject.model;

import com.sparta.hh99_actualproject.dto.ChatRoomDto.ChatRoomReqUpdateDto;
import com.sparta.hh99_actualproject.dto.ChatRoomDto.ChatRoomResUpdateDto;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ChatRoom{

    @Id
    private String chatRoomId;
    /*고민러*/
    @Column
    private String reqTitle;

    @Column
    private String reqCategory;

    @Column
    private String reqNickname;

    @Column
    private String reqGender;

    @Column
    private String reqAge;

    @Column
    private String reqLoveType;

    @Column
    private String reqLovePeriod;

    /*리스너*/

    @Column
    private String resCategory;

    @Column
    private String resNickname;

    @Column
    private String resGender;

    @Column
    private String resAge;

    @Column
    private String resLoveType;


    @Column
    private String resLovePeriod;

    /*이미지*/

    @Column
    private String imgUrl1;

    @Column
    private String imgUrl2;

    @Column
    private String imgUrl3;

    @Column
    private String matchTime;

    @OneToOne
    @JoinColumn(name = "chatExtendId")
    private ChatExtend chatExtend;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id" ,nullable = false)
    private Member member;

    public void reqUpdate(ChatRoomReqUpdateDto reqUpdateDto) {
        this.imgUrl1 = reqUpdateDto.getImgUrl1();
        this.imgUrl2 = reqUpdateDto.getImgUrl2();
        this.imgUrl3 = reqUpdateDto.getImgUrl3();
        this.reqNickname = reqUpdateDto.getReqNickname();
        this.reqTitle = reqUpdateDto.getReqTitle();
        this.reqGender = reqUpdateDto.getReqGender();
        this.reqCategory = reqUpdateDto.getReqCategory();
        this.reqAge = reqUpdateDto.getReqAge();
        this.reqLoveType = reqUpdateDto.getReqLoveType();
        this.reqLovePeriod = reqUpdateDto.getReqLovePeriod();
        this.matchTime = reqUpdateDto.getMatchTime();
    }

    public void resUpdate(ChatRoomResUpdateDto resUpdateDto) {
        this.resNickname = resUpdateDto.getResNickname();
        this.resGender = resUpdateDto.getResGender();
        this.resLoveType = resUpdateDto.getResLoveType();
        this.resCategory = resUpdateDto.getResCategory();
        this.resLovePeriod = resUpdateDto.getResLovePeriod();
        this.resAge = resUpdateDto.getResAge();
        this.matchTime = resUpdateDto.getMatchTime();
    }
}
