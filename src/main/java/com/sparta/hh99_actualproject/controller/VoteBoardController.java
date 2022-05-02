package com.sparta.hh99_actualproject.controller;

import com.sparta.hh99_actualproject.dto.VoteBoardInformationRequestDto;
import com.sparta.hh99_actualproject.dto.VoteBoardResponseDto;
import com.sparta.hh99_actualproject.exception.PrivateResponseBody;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.service.VoteBoardService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
@AllArgsConstructor
@RequestMapping("/anonypost/vote")
public class VoteBoardController {

    private final VoteBoardService voteBoardService;

    @PostMapping()
    public ResponseEntity<PrivateResponseBody> createVoteBoard(@RequestPart(required = false) MultipartFile imgLeftFile,
                                                               @RequestPart(required = false) MultipartFile imgRightFile,
                                                               @RequestPart(value = "information") VoteBoardInformationRequestDto requestDto) {
        String  imgLeftFilePath = null ,imgRightFilePath = null;
        // if(imgLeftFile != null)
        //      String imgLeftFilePath = awsS3Service.saveFiles(imgLeftFile)
        // if(imgRightFile != null)
        //      String imgRightFilePath = awsS3Service.saveFiles(imgRightFile)
//        VoteBoardResponseDto rtval = voteBoardService.createVoteBoard(requestDto, imgLeftFilePath , imgRightFilePath);
        VoteBoardResponseDto rtval = voteBoardService.createVoteBoard(requestDto, imgLeftFile.getOriginalFilename() , imgRightFile.getOriginalFilename());

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK,rtval), HttpStatus.OK);
    }

}
