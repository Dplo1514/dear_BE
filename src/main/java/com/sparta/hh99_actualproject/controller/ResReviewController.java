package com.sparta.hh99_actualproject.controller;


import com.sparta.hh99_actualproject.dto.ReviewRequestDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.PrivateResponseBody;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.service.ResReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class ResReviewController {

    private final ResReviewService resReviewService;

    @PostMapping("/chat/response/review")
    public ResponseEntity<PrivateResponseBody> createReqReview(@RequestBody ReviewRequestDto reviewRequestDto){
        if(reviewRequestDto.isRequestReview())
            throw new PrivateException(StatusCode.IS_NOT_MATCHING_REQUEST_REVIEW);
        if (reviewRequestDto.getTagSelectList().size() != 3)
            throw new PrivateException(StatusCode.IS_NOT_ALLOW_TAG_SELECT_LIST_SIZE);

        resReviewService.createResReview(reviewRequestDto);
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK), HttpStatus.OK);
    }

    /*private final ResReviewService resReviewService;

    @GetMapping("/chat/response/review/{resReviewId}")
    public ResponseEntity<PrivateResponseBody> getResReview(@PathVariable("resReviewId") Long resReviewId){
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK , resReviewService.getResReview(resReviewId)), HttpStatus.OK);
    }



    @PostMapping("/chat/response/review")
    public ResponseEntity<PrivateResponseBody> createResReview(@ModelAttribute ResReviewRequestDto resReviewRequestDto){

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK , resReviewService.createResReview(resReviewRequestDto)), HttpStatus.OK);

    }*/

}

