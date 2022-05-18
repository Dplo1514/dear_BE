package com.sparta.hh99_actualproject.controller;

import com.sparta.hh99_actualproject.dto.CommentDto.CommentRequestDto;
import com.sparta.hh99_actualproject.exception.PrivateResponseBody;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/anonypost")
public class CommentController {
    private final CommentService commentService;


    @GetMapping("/{postId}/comment/{page}")
    public ResponseEntity<PrivateResponseBody> getComment(@PathVariable("postId") Long postId , @PathVariable int page){
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK , commentService.getComment(postId , page)), HttpStatus.OK);
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

    @PostMapping("/board/{postId}/commentLikes/{commentId}")
    public ResponseEntity<PrivateResponseBody> addCommentLikes(@PathVariable("postId") Long postId , @PathVariable("commentId") Long commentId){
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK , commentService.addCommentLikes(postId , commentId)), HttpStatus.OK);
    }

}
