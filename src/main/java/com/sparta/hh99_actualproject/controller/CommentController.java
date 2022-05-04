package com.sparta.hh99_actualproject.controller;
import com.sparta.hh99_actualproject.dto.CommentRequestDto;
import com.sparta.hh99_actualproject.exception.PrivateResponseBody;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.model.Board;
import com.sparta.hh99_actualproject.repository.BoardRepository;
import com.sparta.hh99_actualproject.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/anonypost")
public class CommentController {
    private final CommentService commentService;
    private final BoardRepository boardRepository;

    @GetMapping("/board/{postId}")
    public ResponseEntity<PrivateResponseBody> getboard(@PathVariable("postId") Long postId){

        Board byId = boardRepository.findById(postId).orElseThrow(()
        -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK , byId) , HttpStatus.OK);
    }

    @GetMapping("/{postId}/comment")
    public ResponseEntity<PrivateResponseBody> getComment(@PathVariable("postId") Long postId){
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK , commentService.getComment(postId)), HttpStatus.OK);
    }

    @PostMapping("/board/{postId}/comment")
    public ResponseEntity<PrivateResponseBody> addComment(@PathVariable("postId") Long boardId , @RequestBody CommentRequestDto commentRequestDto){
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK , commentService.addComment(boardId , commentRequestDto)), HttpStatus.OK);
    }

    @PutMapping("/board/{postId}/comment/{commentId}")
    public ResponseEntity<PrivateResponseBody> updateComment(@PathVariable("postId") Long boardId, @PathVariable("commentId") Long commentId , @RequestBody CommentRequestDto commentRequestDto){
        commentService.updateComment(boardId , commentId , commentRequestDto);
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK , null ), HttpStatus.OK);
    }

    @DeleteMapping("/board/{postId}/comment/{commentId}")
    public ResponseEntity<PrivateResponseBody> deleteComment(@PathVariable("postId") Long boardId , @PathVariable("commentId") Long commentId){
        commentService.deleteComment(boardId , commentId);
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK , null ), HttpStatus.OK);
    }
}
