package com.sparta.hh99_actualproject.service;

import com.sparta.hh99_actualproject.dto.CommentDto.CommentLikesResponseDto;
import com.sparta.hh99_actualproject.dto.CommentDto.CommentRequestDto;
import com.sparta.hh99_actualproject.dto.CommentDto.CommentResponseDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.*;
import com.sparta.hh99_actualproject.repository.BoardRepository;
import com.sparta.hh99_actualproject.repository.CommentRepository;
import com.sparta.hh99_actualproject.repository.MemberRepository;
import com.sparta.hh99_actualproject.service.validator.Validator;
import com.sparta.hh99_actualproject.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    private final Validator validator;

    private final ScoreService scoreService;

    private final NotificationService notificationService;

    //해당 게시글의 댓글 모두 리턴
    @Transactional
    public List<CommentResponseDto> getComment(Long postId, int page) {

        PageRequest pageRequest = PageRequest.of(page - 1, 2);

        Page<Comment> commentList = commentRepository.findAllByBoardBoardPostIdOrderByCreatedAtDesc(postId, pageRequest);
        List<Comment> totalComments = commentRepository.findAllByBoardBoardPostId(postId);

        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

        //리스폰스 dto에 빌더하고 list에넣고 리턴
        for (Comment comment : commentList) {
            CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                    .commentId(comment.getCommentId())
                    .member(comment.getMember().getMemberId())
                    .comment(comment.getContent())
                    .createdAt(String.valueOf(comment.getCreatedAt()))
                    .totalPages(commentList.getTotalPages())
                    .likes(comment.getIsLike())
                    .boardPostId(comment.getBoard().getBoardPostId())
                    .totalComments(totalComments.size())
                    .build();
            commentResponseDtoList.add(commentResponseDto);
        }
        return commentResponseDtoList;
    }

    //댓글 저장
    @Transactional
    public CommentResponseDto addComment(Long boardId, CommentRequestDto commentRequestDto) {
        //인터셉터의 jwt token의 memberid를 받아온다.
        String memberId = SecurityUtil.getCurrentMemberId();
        System.out.println("memberId = " + memberId);
        //content 값이 null로 들어온 경우 execption을 발생시킨다.
        validator.hasNullCheckComment(commentRequestDto);

        //memberId와 일치하는 멤버를 찾아온다.
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        //boardId와 일치하는 게시글을 찾아온다.
        Board board = boardRepository.findById(boardId).orElseThrow(
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

        notificationService.saveNotification(board.getMember().getMemberId(),NotiTypeEnum.COMMENT,board.getTitle(), board.getBoardPostId());

        PageRequest pageRequest = PageRequest.of(0 , 2);

        List<Comment> totalComments = commentRepository.findAllByBoardBoardPostId(boardId);
        Page<Comment> findTotalPages = commentRepository.findAllByBoardBoardPostIdOrderByCreatedAtDesc(saveComment.getBoard().getBoardPostId() , pageRequest);


        //리턴해주기위해 ResponseDto에 빌드한다.
        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .member(saveComment.getMember().getMemberId())
                .commentId(saveComment.getCommentId())
                .createdAt(String.valueOf(saveComment.getCreatedAt()))
                .comment(saveComment.getContent())
                .boardPostId(saveComment.getBoard().getBoardPostId())
                .likes(saveComment.getIsLike())
                .totalComments(totalComments.size())
                .totalPages(findTotalPages.getTotalPages())
                .build();

        //저장한 댓글을 content로 찾아 저장된 댓글을 바로 return한다.
        return commentResponseDto;
    }


    //댓글 수정
    @Transactional
    public void updateComment(Long boardId, Long commentId, CommentRequestDto commentRequestDto) {
        //인터셉터의 jwt token의 memberid를 받아온다.
        String memberId = SecurityUtil.getCurrentMemberId();

        //content 값이 null로 들어온 경우 execption을 발생시킨다.
        validator.hasNullCheckComment(commentRequestDto);

        //commentId로 댓글을 찾아온다.
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_COMMENT)
        );

        //사용자가 채택된 댓글을 수정시도했을 경우 exception을 발생시킨다.
        validator.hasValidCheckCommentIsAccepted(comment);

        //사용자가 권한없는 댓글을 수정시도했을 경우 exception을 발생시킨다.
        validator.hasValidCheckAuthorityComment(memberId, comment);

        //사용자가 게시글에 존재하지않는 댓글을 수정하려할 경우 exception을 발생시킨다.
        validator.hasValidCheckEffectiveComment(boardId, comment);

        comment.setContent(commentRequestDto.getComment());
    }


    //댓글 삭제
    @Transactional
    public void deleteComment(Long boardId, Long commentId) {
        //인터셉터의 jwt token의 memberid를 받아온다.
        String memberId = SecurityUtil.getCurrentMemberId();

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_COMMENT));

        //사용자가 권한없는 댓글을 삭제시도했을 경우 exception을 발생시킨다.
        validator.hasValidCheckAuthorityComment(memberId, comment);

        //사용자가 게시글에 존재하지않는 댓글을 삭제하려할 경우 exception을 발생시킨다.
        validator.hasValidCheckEffectiveComment(boardId, comment);

        commentRepository.delete(comment);
    }

    @Transactional
    public CommentLikesResponseDto addCommentLikes(Long postId, Long commentId) {
        //인터셉터의 jwt token의 memberid를 받아온다.
        String memberId = SecurityUtil.getCurrentMemberId();

        //파라미터 commentId를 사용해 댓글을 찾아온다.
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        //댓글의 게시글의 작성자와 로그인한 작성자가 일치하지않으면
        validator.hasValidCheckAuthorityCommentLike(memberId , comment);
        validator.isValidCheckCommentSelfChoose(memberId, comment);
        //댓글 작성시
        CommentLikesResponseDto commentLikesResponseDto = new CommentLikesResponseDto();

        //댓글의 isLike가 false이면 true로 true이면 false로
        //댓글의 채택 , 취소 여부에 따라 SCORE를 최신화해준다.
        if (!comment.getIsLike()) {
                comment.setIsLike(true);// 댓글 작성자가 멤버에 들어가야한다.
                scoreService.calculateMemberScore(comment.getMember().getMemberId(), 0.5F, ScoreType.COMMENT_SELECTION);

                notificationService.saveNotification(comment.getMember().getMemberId(), NotiTypeEnum.CHOICE, comment.getBoard().getTitle(), comment.getBoard().getBoardPostId());
        } else {
            comment.setIsLike(false);
            scoreService.calculateMemberScore(comment.getMember().getMemberId(), -0.5F, ScoreType.COMMENT_SELECTION);
        }

        commentLikesResponseDto.setLikes(comment.getIsLike());

        return commentLikesResponseDto;
    }
}