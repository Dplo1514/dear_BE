package com.sparta.hh99_actualproject;

import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.ChatRoom;
import com.sparta.hh99_actualproject.model.Member;
import com.sparta.hh99_actualproject.repository.ChatRoomRepository;
import com.sparta.hh99_actualproject.repository.MemberRepository;
import org.joda.time.DateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;

@SpringBootTest
class Hh99ActualProjectApplicationTests {
    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    MemberRepository memberRepository;


    @Test
    @Order(1)
    @DisplayName("리워드 적립 시간계산 테스트코드")
    void rewardStackTime() {
        rewardStackTimeTest("2022.01.02 02:10" , "test");
    }

    void rewardStackTimeTest(String terminationTime , String sessionId){

        ChatRoom chatRoom = chatRoomRepository.findById(sessionId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_CHAT_ROOM));

        //채팅방의 닉네임을 활용해 request유저와 response유저를 찾아온다.
        Member reqUser = memberRepository.findMemberByNickname(chatRoom.getReqNickname()).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        Member resUser = memberRepository.findMemberByNickname(chatRoom.getResNickname()).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        //받아온 종료시간을 dateTime으로 형변환
        LocalDateTime terminationDateTime = LocalDateTime.parse(terminationTime ,DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));

        //dn에서 가져온 매칭 시간을 datetime으로 형변환
        LocalDateTime startChatTime = LocalDateTime.parse(chatRoom.getMatchTime() ,DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));;

        //만약 두 시간의 날짜가 다르면 자정이 지났음을 의미 1시간을 minus함으로써 시간의 비교가 가능해진다.
        if (terminationDateTime.getDayOfWeek() != startChatTime.getDayOfWeek()){
            terminationDateTime = terminationDateTime.minusHours(1);
        }

        //종료시간에서 시작시간을 차감해 채팅시간을 구한다.
        LocalDateTime chatTime = terminationDateTime.minusHours(startChatTime.getHour()).minusMinutes(startChatTime.getMinute());

        //채팅시간이 3분보다 크면 req멤버의 리워드의 차감이 일어난다.
        //채팅시간이 7분보다 크면 res멤버의 리워드의 적립이 일어난다.
        if (chatTime.getMinute() > 3){
            reqUser.setReward(reqUser.getReward() - 1);
        }

        if (chatTime.getMinute() > 7){
            resUser.setReward(resUser.getReward() + 2);
        }
    }

}
