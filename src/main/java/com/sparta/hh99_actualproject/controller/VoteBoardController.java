package com.sparta.hh99_actualproject.controller;

import com.sparta.hh99_actualproject.dto.*;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.PrivateResponseBody;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.service.AwsS3Service;
import com.sparta.hh99_actualproject.service.SelectionService;
import com.sparta.hh99_actualproject.service.VoteBoardService;
import com.sparta.hh99_actualproject.service.validator.Validator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/anonypost/vote")
public class VoteBoardController {

    private final Validator validator;
    private final VoteBoardService voteBoardService;
    private final SelectionService selectionService;

    @PostMapping()
    public ResponseEntity<PrivateResponseBody> createVoteBoard(@ModelAttribute VoteBoardRequestDto requestDto) {
        VoteBoardResponseDto rtval = voteBoardService.createVoteBoard(requestDto);
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,rtval), HttpStatus.OK);

    }

    @GetMapping("/{postId}")
    public ResponseEntity<PrivateResponseBody> getVoteBoard(@PathVariable("postId") Long postId) {
        VoteBoardResponseDto rtval = voteBoardService.getVoteBoard(postId);
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,rtval), HttpStatus.OK);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<PrivateResponseBody> deleteVoteBoard(@PathVariable("postId") Long postId) {
        voteBoardService.deleteVoteBoard(postId);
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK), HttpStatus.OK);
    }

    @PostMapping("/{postId}/voteSelect")
    public ResponseEntity<PrivateResponseBody> selectVoteContent(@PathVariable("postId") Long postId , @RequestParam("selectionNum") Integer selectionNum) {
        if(validator.isValidSelectionNum(selectionNum)){
            throw new PrivateException(StatusCode.WRONG_INPUT_VOTE_SELECTION);
        }
        SelectionResponseDto selectionResponseDto = selectionService.selectVoteContent(postId,selectionNum);

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,selectionResponseDto), HttpStatus.OK);
    }
}
