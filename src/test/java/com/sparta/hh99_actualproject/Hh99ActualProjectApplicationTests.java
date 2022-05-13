package com.sparta.hh99_actualproject;

import com.sparta.hh99_actualproject.dto.CommentRequestDto;
import com.sparta.hh99_actualproject.dto.CommentResponseDto;
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
import com.sparta.hh99_actualproject.util.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
        rewardStackTimeTest("2022-05-14T05:16:38.554" , "ses_E7rrzxJL40");
    }

    void rewardStackTimeTest(String terminationTime , String sessionId){

        ChatRoom chatRoom = chatRoomRepository.findById(sessionId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_CHAT_ROOM));

        //채팅방의 닉네임을 활용해 request유저와 response유저를 찾아온다.
        Member reqUser = memberRepository.findByNickname(chatRoom.getReqNickname()).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        System.out.println(reqUser.getNickname());


        Member resUser = memberRepository.findByNickname(chatRoom.getResNickname()).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));


        //받아온 종료시간을 dateTime으로 형변환
        LocalDateTime terminationDateTime = LocalDateTime.parse(terminationTime ,DateTimeFormatter.ISO_INSTANT);

        //dn에서 가져온 매칭 시간을 datetime으로 형변환
        LocalDateTime startChatTime = LocalDateTime.parse("2022-05-14T05:16:38.554" ,DateTimeFormatter.ISO_INSTANT);

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
        Long postId = Long.valueOf(1);
        Long commentId = Long.valueOf(5);
        String memberId = "test9999";

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

    @Test
    @Order(3)
    @DisplayName("댓글 좋아요 트러블 슈팅 테스트 코드")
    void addComment() {

        String memberId = "plo1514";

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setComment("test");

        //memberId와 일치하는 멤버를 찾아온다.
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        //boardId와 일치하는 게시글을 찾아온다.
        Board board = boardRepository.findById(1L).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_POST));

        //저장할 댓글을 build한다.
        Comment comment = Comment.builder()
                .board(board)
                .member(member)
                .content(commentRequestDto.getComment())
                .isLike(false)
                .build();

        //댓글을 저장하고 저장된 댓글을 바로 받는다.
        Comment saveComment = commentRepository.save(comment);


        //리턴해주기위해 ResponseDto에 빌드한다.
        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .member(saveComment.getMember().getMemberId())
                .commentId(saveComment.getCommentId())
                .createdAt(saveComment.getCreatedAt())
                .comment(saveComment.getContent())
                .boardPostId(saveComment.getBoard().getBoardPostId())
                .likes(saveComment.getIsLike())
                .build();

    }

    @Test
    @Order(4)
    @DisplayName("zoneDateTest")
    void zoneDateTimeTest(){
        LocalDateTime now = LocalDateTime.now();
        String test = now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
        System.out.println("test = " + test);

        LocalDateTime startChatTime = LocalDateTime.parse( test , DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));

        System.out.println(startChatTime);
    }
//    @Test
//    @Order(3)
//    @DisplayName("댓글 좋아요 트러블 슈팅 테스트 코드")
//    void addComment() {
//
//        String memberId = "plo1514";
//
//        CommentRequestDto commentRequestDto = new CommentRequestDto();
//        commentRequestDto.setComment("test");
//
//        //memberId와 일치하는 멤버를 찾아온다.
//        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
//                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));
//
//        //boardId와 일치하는 게시글을 찾아온다.
//        Board board = boardRepository.findById(1L).orElseThrow(
//                () -> new PrivateException(StatusCode.NOT_FOUND_POST));
//
//        //저장할 댓글을 build한다.
//        Comment comment = Comment.builder()
//                .board(board)
//                .member(member)
//                .content(commentRequestDto.getComment())
//                .isLike(false)
//                .build();
//
//        //댓글을 저장하고 저장된 댓글을 바로 받는다.
//        Comment saveComment = commentRepository.save(comment);
//
//
//        //리턴해주기위해 ResponseDto에 빌드한다.
//        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
//                .member(saveComment.getMember().getMemberId())
//                .commentId(saveComment.getCommentId())
//                .createdAt(saveComment.getCreatedAt())
//                .comment(saveComment.getContent())
//                .boardPostId(saveComment.getBoard().getBoardPostId())
//                .likes(saveComment.getIsLike())
//                .build();
//
//    }
}
