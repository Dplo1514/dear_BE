package com.sparta.hh99_actualproject.controller;

import com.sparta.hh99_actualproject.dto.VoteBoardRequestDto;
import com.sparta.hh99_actualproject.dto.VoteBoardResponseDto;
import com.sparta.hh99_actualproject.exception.PrivateResponseBody;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.service.VoteBoardService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/anonypost/vote")
public class VoteBoardController {

    private final VoteBoardService voteBoardService;

    @PostMapping()
    public ResponseEntity<PrivateResponseBody> createVoteBoard(@ModelAttribute VoteBoardRequestDto requestDto) {
        String  imgLeftFilePath = null ,imgRightFilePath = null;
        imgLeftFilePath = requestDto.getImgLeftFile() == null ? null : requestDto.getImgLeftFile().getOriginalFilename();
        imgRightFilePath = requestDto.getImgRightFile() == null ? null : requestDto.getImgRightFile().getOriginalFilename();
        // if(requestDto.getImgLeftFile() != null)
        //      String imgLeftFilePath = awsS3Service.saveFiles(requestDto.getImgLeftFile());
        // if(requestDto.getImgRightFile != null)
        //      String imgRightFilePath = awsS3Service.saveFiles(requestDto.getImgRightFile());
        VoteBoardResponseDto rtval = voteBoardService.createVoteBoard(requestDto, imgLeftFilePath , imgRightFilePath);
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,rtval), HttpStatus.OK);

    }

}
