package com.sparta.hh99_actualproject.controller;

import com.sparta.hh99_actualproject.dto.CommentRequestDto;
import com.sparta.hh99_actualproject.dto.CommentResponseDto;
import com.sparta.hh99_actualproject.exception.PrivateResponseBody;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/anonypost/{postId}/comment")
    public ResponseEntity<PrivateResponseBody> getComment(@RequestParam("postId")Long postId){
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK , commentService.getComment(postId)), HttpStatus.OK);
    }

    @PostMapping("/anonypost/board/{postId}/comment")
    public ResponseEntity<PrivateResponseBody> addComment(@RequestParam("postId") String boardId , CommentRequestDto commentRequestDto){
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK , commentService.addComment(boardId , commentRequestDto)), HttpStatus.OK);
    }

    @PutMapping("/anonypost/board/{postId}/comment/{commentId}")
    public ResponseEntity<PrivateResponseBody> updateComment(@RequestParam("postId") String boardId,@RequestParam("commentId") Long commentId ,CommentRequestDto commentRequestDto){
        commentService.updateComment(boardId , commentId , commentRequestDto);
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK , null ), HttpStatus.OK);
    }

    @DeleteMapping("/anonypost/board/{postId}/comment/{commentId}")
    public ResponseEntity<PrivateResponseBody> deleteComment(@RequestParam("postId") String boardId , @RequestParam("commentId") Long commentId){
        commentService.deleteComment(boardId , commentId);
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK , null ), HttpStatus.OK);
    }
}
