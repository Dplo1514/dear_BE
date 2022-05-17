package com.sparta.hh99_actualproject;


import com.sparta.hh99_actualproject.dto.BoardResponseDto.PostListResponseDto;
import com.sparta.hh99_actualproject.dto.CommentDto.CommentResponseDto;
import com.sparta.hh99_actualproject.dto.MemberResponseDto.ResTagResponseDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.*;
import com.sparta.hh99_actualproject.repository.*;
import com.sparta.hh99_actualproject.service.ScoreService;
import com.sparta.hh99_actualproject.service.ScoreType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    @Autowired
    ResponseTagRepository responseTagRepository;

    @Autowired
    ScoreRepository scoreRepository;

    @Autowired
    LikesRepository likesRepository;

    @Autowired
    VoteBoardRepository voteBoardRepository;

    @Test
    @Order(1)
    @DisplayName("리워드 적립 시간계산 테스트코드")
    void rewardStackTime() {
        rewardStackTimeTest("2022-05-14T05:16:38.554", "ses_E7rrzxJL40");
    }

    void rewardStackTimeTest(String terminationTime, String sessionId) {

        ChatRoom chatRoom = chatRoomRepository.findById(sessionId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_CHAT_ROOM));

        //채팅방의 닉네임을 활용해 request유저와 response유저를 찾아온다.
        Member reqUser = memberRepository.findByNickname(chatRoom.getReqNickname()).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        System.out.println(reqUser.getNickname());


        Member resUser = memberRepository.findByNickname(chatRoom.getResNickname()).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));


        //받아온 종료시간을 dateTime으로 형변환
        LocalDateTime terminationDateTime = LocalDateTime.parse(terminationTime, DateTimeFormatter.ISO_INSTANT);

        //dn에서 가져온 매칭 시간을 datetime으로 형변환
        LocalDateTime startChatTime = LocalDateTime.parse("2022-05-14T05:16:38.554", DateTimeFormatter.ISO_INSTANT);

        //만약 두 시간의 날짜가 다르면 자정이 지났음을 의미 1시간을 minus함으로써 시간의 비교가 가능해진다.
        if (terminationDateTime.getDayOfWeek() != startChatTime.getDayOfWeek()) {
            terminationDateTime = terminationDateTime.minusHours(1);
        }

        //종료시간에서 시작시간을 차감해 채팅시간을 구한다.
        LocalDateTime chatTime = terminationDateTime.minusHours(startChatTime.getHour()).minusMinutes(startChatTime.getMinute());

        //채팅시간이 3분보다 크면 req멤버의 리워드의 차감이 일어난다.
        //채팅시간이 7분보다 크면 res멤버의 리워드의 적립이 일어난다.
        if (chatTime.getMinute() > 3) {
            reqUser.setReward(reqUser.getReward() - 1);
        }

        if (chatTime.getMinute() > 7) {
            resUser.setReward(resUser.getReward() + 2);
        }
    }

    @Test
    @Order(2)
    @DisplayName("댓글 좋아요 기능 테스트 코드")
    void commentLikeTest() {
        Long postId = Long.valueOf(1);
        Long commentId = Long.valueOf(5);
        String memberId = "test9999";

        //파라미터 commentId를 사용해 멤버를 찾아온다
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        Board board = boardRepository.findById(postId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        //댓글의 게시글의 작성자와 로그인한 작성자가 일치하지않으면
        if (!board.getMember().getMemberId().equals(memberId)) {
            throw new PrivateException(StatusCode.WRONG_ACCESS_COMMENTLIKES);
        }

        if (comment.getIsLike()) {
            comment.setIsLike(false);
            scoreService.calculateMemberScore(memberId, 0.5F, ScoreType.COMMENT_SELECTION);
            commentRepository.save(comment);
        } else if (!(comment.getIsLike())) {
            comment.setIsLike(true);
            scoreService.calculateMemberScore(memberId, -0.5F, ScoreType.COMMENT_SELECTION);
            commentRepository.save(comment);
        }

        System.out.println(comment.getIsLike());
    }

    @Test
    @Order(3)
    @DisplayName("댓글 좋아요 트러블 슈팅 테스트 코드")
    void addComment() {

        String memberId = "plo1514";


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
                .content("commentRequestDto.getComment()")
                .isLike(false)
                .build();

        //댓글을 저장하고 저장된 댓글을 바로 받는다.
        Comment saveComment = commentRepository.save(comment);

        //리턴해주기위해 ResponseDto에 빌드한다.
        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .member(saveComment.getMember().getMemberId())
                .commentId(saveComment.getCommentId())
                .createdAt(String.valueOf(saveComment.getCreatedAt()))
                .comment(saveComment.getContent())
                .boardPostId(saveComment.getBoard().getBoardPostId())
                .likes(saveComment.getIsLike())
                .build();

    }

    @Test
    @Order(4)
    @DisplayName("zoneDateTest")
    void zoneDateTimeTest() {
        LocalDateTime now = LocalDateTime.now();
        String test = now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
        System.out.println("test = " + test);

        LocalDateTime startChatTime = LocalDateTime.parse(test, DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));

        System.out.println(startChatTime);
    }

    @Test
    @Order(5)
    @DisplayName("res태그중 가장 큰 값 두개 가져오기")
    void getResTest() {
        //멤버가 획득한 response태그들을 찾아온다.
        ResponseTag responseTag = responseTagRepository.findByMemberId("plo1514");
        //return값을 담을 Dto
        ResTagResponseDto resTagResponseDto = new ResTagResponseDto();

        //TagNumber별 리턴해야할 태그 값을 set해줄 Map
        ConcurrentHashMap<Integer , String> resTagMapContent = new ConcurrentHashMap<>();
        resTagMapContent.put(1 , "공감을 잘해줬어요");
        resTagMapContent.put(2 , "대화가 즐거웠어요");
        resTagMapContent.put(3 , "감수성이 풍부했어요");
        resTagMapContent.put(4 , "시원하게 팩트폭격을 해줘요");
        resTagMapContent.put(5 , "명쾌한 해결책을 알려줘요");

        //ResTag별로 인덱스를 지정하는 방법
        //1. 맵에 Res태그별 키값을 지정해준다.
        //2. value인 Res태그중 가장 큰 값을 두개 찾는다.
        //3. 가장 큰 값의 key 두개로 String맵의 key를 인덱스한다.
        ConcurrentHashMap<Integer , Integer> resTagIdx = new ConcurrentHashMap<>();

            resTagIdx.put(1 , responseTag.getResTag1Num());
            resTagIdx.put(2 , responseTag.getResTag2Num());
            resTagIdx.put(3 , responseTag.getResTag3Num());
            resTagIdx.put(4 , responseTag.getResTag4Num());
            resTagIdx.put(5 , responseTag.getResTag5Num());

        //value를 기준으로 오름차순이 가능하게하는 comparingByValue함수를 사용하기위해
        //List에 Map.Entry로 resTagIdx를 할당해준다.
        //Map.Entry : Map을 For 문에서 돌려줄 경우 , Map에서 strem , 정렬 등을 필요할 때 사용하는 인터페이스
        //리스트의 Iterator와 비슷한 개념이라 생각하면 좋을 것 같다.
        //1. Map.Entry를 제네릭스로 받는 리스트 객체를 생성
        //2. 링크드 리스트 :
        //3. resTagIdx.map.entrySet() : 맵의 K , V 전체를 가져와서 리스트에 할당한다..
        List<Map.Entry<Integer, Integer>> entryList = new ArrayList<>(resTagIdx.entrySet());
        //Map.Entry.comparingByValue() : 해당 map의 value값을 기준으로 정렬한다.
        entryList.sort(Map.Entry.comparingByValue());

        System.out.println("entryList Max1 key = " + entryList.get(4).getKey());
        System.out.println("entryList Max2 key = " + entryList.get(3).getKey());


        //value값으로 정렬된 entryList의 3 , 4번째는 resTagIdx의 key 중 valye값이 가장 큰 키 2개를 의미
        //이는 resTag의 갯수가 가장 많은 것의 key를 의미한다.
        //해당 키로 resTag별로 미리 리턴 값(value)을 지정해준 map의 idx함으로써 가장 큰 값 두개의 String을 인덱스할 수 있다.
        resTagResponseDto.setResTag1(resTagMapContent.get(entryList.get(3).getKey()));
        resTagResponseDto.setResTag2(resTagMapContent.get(entryList.get(4).getKey()));

        System.out.println("entryList = " + resTagResponseDto.getResTag1());
        System.out.println("entryList = " + resTagResponseDto.getResTag2());
    }

    @Test
    @Order(6)
    @DisplayName("댓글 페이징 테스트")
    void pagination(){
        Board board = boardRepository.findById(1L).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_POST));

        PageRequest pageRequest = PageRequest.of(6 , 3);
        Page<Comment> allByBoardOrderByCreatedAtDesc = null;
        for (int i = 0 ; i < 7  ; i ++){
            pageRequest = PageRequest.of(i , 3);
            allByBoardOrderByCreatedAtDesc = commentRepository.findAllByBoardOrderByCreatedAtDesc(board, pageRequest);
            System.out.println(allByBoardOrderByCreatedAtDesc.getContent().get(0).getContent());
            System.out.println(allByBoardOrderByCreatedAtDesc.getContent().get(1).getContent());
            System.out.println(allByBoardOrderByCreatedAtDesc.getContent().get(2).getContent());
        }

        allByBoardOrderByCreatedAtDesc = commentRepository.findAllByBoardOrderByCreatedAtDesc(board, pageRequest);
        for (Comment comment : allByBoardOrderByCreatedAtDesc) {
            System.out.println(comment.getContent());
        }
    }

    @Test
    @Order(7)
    @DisplayName("스트링 유틸 테스트")
    void hasTextTest(){
        String test = null;
        boolean b = StringUtils.hasText(test);
        System.out.println("b = " + b);
    }

    @Test
    @Order(8)
    @DisplayName("랭킹 찾기")
    void findRank(){
        List<Score> findAllScore = scoreRepository.findAllByOrderByScoreDesc();

        List<Member> rankMemberList = new ArrayList<>(5);

        for (Score score : findAllScore) {
            Member findRankMember = memberRepository.findByMemberId(score.getMemberId()).orElseThrow(
                    () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));
            rankMemberList.add(findRankMember);

            System.out.println("score = " + score.getScore());

            if (rankMemberList.size() >= 5){
                break;
            }
        }
        for (Member member : rankMemberList) {
            System.out.println("member = " + member.getMemberId());
        }
    }

    @Test
    @Order(10)
    @DisplayName("멤버의 게시글 찾기")
    @Transactional
    void findMemberBoard(){
        //유저의 최종 리턴 값이 할당될 리스트
        List<PostListResponseDto> postListResponseDtoList = new ArrayList<>();
        PostListResponseDto postListResponseDto = new PostListResponseDto();

        List<Board> boardList = boardRepository.findAllByMemberMemberIdOrderByCreatedAtDesc("queen1");

        for (Board board : boardList) {
            postListResponseDto.builder()
                    .postId(board.getBoardPostId())
                    .title(board.getTitle())
                    .createdAt(String.valueOf(board.getCreatedAt()))
                    .category(board.getCategory())
                    .comments(board.getCommentList().size())
                    .likes(board.getLikesList().size())
                    .type("Board")
                    .build();
            postListResponseDtoList.add(postListResponseDto);
        }

        List<VoteBoard> voteBoardList = voteBoardRepository.findAllByMemberMemberIdOrderByCreatedAtDesc("queen1");
        for (VoteBoard voteBoard : voteBoardList) {
            postListResponseDto.builder()
                    .postId(voteBoard.getVoteBoardId())
                    .title(voteBoard.getTitle())
                    .createdAt(String.valueOf(voteBoard.getCreatedAt()))
                    .type("Vote")
                    .build();
            postListResponseDtoList.add(postListResponseDto);

        }
    }

}
