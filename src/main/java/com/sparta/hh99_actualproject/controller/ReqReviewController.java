package com.sparta.hh99_actualproject.controller;

import com.sparta.hh99_actualproject.dto.ReviewRequestDto;
import com.sparta.hh99_actualproject.exception.PrivateException;
import com.sparta.hh99_actualproject.exception.PrivateResponseBody;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.service.ReqReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class ReqReviewController {
    private final ReqReviewService reqReviewService;

    @PostMapping("/chat/request/review")
    public ResponseEntity<PrivateResponseBody> createReqReview(@RequestBody ReviewRequestDto reviewRequestDto){
        if(!reviewRequestDto.isRequestReview())
            throw new PrivateException(StatusCode.IS_NOT_MATCHING_REQUEST_REVIEW);
        if (reviewRequestDto.getTagSelectList().size() != 5)
            throw new PrivateException(StatusCode.IS_NOT_ALLOW_TAG_SELECT_LIST_SIZE);

        reqReviewService.createReqReview(reviewRequestDto);
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK), HttpStatus.OK);
    }
}

