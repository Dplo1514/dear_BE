package com.sparta.hh99_actualproject;

import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.Board;
import com.sparta.hh99_actualproject.model.ChatRoom;
import com.sparta.hh99_actualproject.model.Comment;
import com.sparta.hh99_actualproject.model.Member;
import com.sparta.hh99_actualproject.repository.BoardRepository;
import com.sparta.hh99_actualproject.repository.ChatRoomRepository;
import com.sparta.hh99_actualproject.repository.CommentRepository;
import com.sparta.hh99_actualproject.repository.MemberRepository;
import com.sparta.hh99_actualproject.service.ScoreService;
import com.sparta.hh99_actualproject.service.ScoreType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest
class Hh99ActualProjectApplicationTests {
    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    ScoreService scoreService;


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

    @Test
    @Order(2)
    @DisplayName("댓글 좋아요 기능 테스트 코드")
    void commentLikeTest(){
        Long postId = Long.valueOf(11);
        Long commentId = Long.valueOf(1);
        String memberId = "queen123";

        //파라미터 commentId를 사용해 멤버를 찾아온다
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        Board board = boardRepository.findById(postId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        //댓글의 게시글의 작성자와 로그인한 작성자가 일치하지않으면
        if(!board.getMember().getMemberId().equals(memberId)){
            throw new PrivateException(StatusCode.WRONG_ACCESS_COMMENTLIKES);
        }

        if (comment.getIsLike()) {
            comment.setIsLike(false);
            scoreService.calculateMemberScore(memberId ,0.5F ,ScoreType.COMMENT_SELECTION);
            commentRepository.save(comment);
        } else if (!(comment.getIsLike())) {
            comment.setIsLike(true);
            scoreService.calculateMemberScore(memberId ,-0.5F ,ScoreType.COMMENT_SELECTION);
            commentRepository.save(comment);
        }

        System.out.println(comment.getIsLike());
    }
}
