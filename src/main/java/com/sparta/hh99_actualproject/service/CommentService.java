package com.sparta.hh99_actualproject.service;

import com.sparta.hh99_actualproject.dto.CommentRequestDto;
import com.sparta.hh99_actualproject.dto.CommentResponseDto;
import com.sparta.hh99_actualproject.dto.CommentResponseDto.CommentLikesResponseDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.Board;
import com.sparta.hh99_actualproject.model.Comment;
import com.sparta.hh99_actualproject.model.CommentLikes;
import com.sparta.hh99_actualproject.model.Member;
import com.sparta.hh99_actualproject.repository.BoardRepository;
import com.sparta.hh99_actualproject.repository.CommentLikesRepository;
import com.sparta.hh99_actualproject.repository.CommentRepository;
import com.sparta.hh99_actualproject.repository.MemberRepository;
import com.sparta.hh99_actualproject.service.validator.Validator;
import com.sparta.hh99_actualproject.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
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

    private final CommentLikesRepository commentLikesRepository;
    private final Validator validator;

    //해당 게시글의 댓글 모두 리턴
    @Transactional
    public List<CommentResponseDto> getComment(Long postId) {
        Board board = boardRepository.findById(postId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        List<Comment> commentList = commentRepository.findAllByBoardOrderByCreatedAtDesc(board).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_MEMBER));

        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

        //리스폰스 dto에 빌더하고 list에넣고 리턴
        for (Comment comment : commentList) {
            CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                    .commentId(comment.getCommentId())
                    .member(comment.getMember().getMemberId())
                    .comment(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .likes(isCommentLikes(comment.getCommentLikes()))
                    .boardPostId(comment.getBoard().getBoardPostId())
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
                .likes(isCommentLikes(comment.getCommentLikes()))
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

        //사용자가 권한없는 댓글을 수정시도했을 경우 exception을 발생시킨다.
        validator.hasValidCheckAuthorityComment(memberId, comment);

        //사용자가 게시글에 존재하지않는 댓글을 수정하려할 경우 exception을 발생시킨다.
        validator.hasValidCheckEffectiveComment(boardId, comment);

        comment.update(commentRequestDto);
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

    public CommentLikesResponseDto addCommentLikes(Long boardId, Long commentId) {
        //인터셉터의 jwt token의 memberid를 받아온다.
        String memberId = SecurityUtil.getCurrentMemberId();
        CommentLikesResponseDto commentLikesResponseDto = new CommentLikesResponseDto();
        //파라미터로 받은 boardId로 해당하는 board를 찾는다.
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_POST));

        //만약 해당 board의 작성자인 member와 로그인한 member가 일치하지 않는다면 권한없음 예외를 발생시킨다.
        validator.hasValidCheckAuthorityCommentLike(memberId, board);

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new PrivateException(StatusCode.NOT_FOUND_COMMENT));

        //comment의 commentLikes가 null이면 commentLikes를 빌드하고 저장
        if (comment.getCommentLikes() == null) {

            CommentLikes commentLikes = CommentLikes.builder()
                    .comment(comment)
                    .memberId(memberId)
                    .build();

            comment.likeUpdate(commentLikes);
            commentLikesRepository.save(commentLikes);
            commentLikesResponseDto.setLikes(true);
            return commentLikesResponseDto;
        }

        //comment의 commentLikes가 null이 아니며 , 게시글의 작성자와 이름이 같으면
        if (comment.getCommentLikes() != null && comment.getCommentLikes().getMemberId().equals(memberId)) {
            CommentLikes commentLikes = comment.getCommentLikes();
            comment.likeUpdate(null);
            commentLikesRepository.delete(commentLikes);
            commentLikesResponseDto.setLikes(false);
            return commentLikesResponseDto;
        }
        return commentLikesResponseDto;
    }

    //commentLikes를 파라미터로 받아 true false를 리턴하는 메서드
    public static boolean isCommentLikes(CommentLikes commentLikes) {
        return commentLikes != null;
    }
}
