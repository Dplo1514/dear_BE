package com.sparta.hh99_actualproject.controller;

import com.sparta.hh99_actualproject.exception.PrivateResponseBody;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.service.MainService;
import com.sparta.hh99_actualproject.service.VoteBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/main")
public class MainController {
    private final MainService mainService;

    private final VoteBoardService voteBoardService;


    @GetMapping(value = "/ranking/member")
    ResponseEntity<PrivateResponseBody> getRankingMember(){
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK , mainService.getRankMember()) , HttpStatus.OK);
    }

    @GetMapping(value = "/ranking/board")
    ResponseEntity<PrivateResponseBody> getRankingBoard() {
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK , mainService.getRankingBoard()) , HttpStatus.OK);
    }

    @GetMapping(value = "/ranking/voteBoard")
    ResponseEntity<PrivateResponseBody> getRankingVoteBoard(){
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK , voteBoardService.getTop12RankVoteBoard()) , HttpStatus.OK);
    }

    @GetMapping(value = "/serviceComment")
    ResponseEntity<PrivateResponseBody> getServiceReview(){
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK , mainService.getServiceReview()) , HttpStatus.OK);
    }
}
