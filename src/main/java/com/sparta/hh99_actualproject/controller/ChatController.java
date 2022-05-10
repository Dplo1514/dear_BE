package com.sparta.hh99_actualproject.controller;

import com.sparta.hh99_actualproject.dto.ChatRoomDto.ChatRoomResRequestDto;
import com.sparta.hh99_actualproject.exception.PrivateResponseBody;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.service.ChatService;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.sparta.hh99_actualproject.dto.ChatRoomDto.ChatRoomReqRequestDto;


@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    @PostMapping(value = "/request")
    ResponseEntity<PrivateResponseBody> getTokenReq(@ModelAttribute ChatRoomReqRequestDto requestDto) throws OpenViduJavaClientException, OpenViduHttpException {
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK , chatService.createTokenReq(requestDto)) , HttpStatus.OK);
    }

    @PostMapping(value = "/response")
    ResponseEntity<PrivateResponseBody> getTokenRes(@RequestBody ChatRoomResRequestDto requestDto) throws OpenViduJavaClientException, OpenViduHttpException {
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK , chatService.createTokenRes(requestDto)) , HttpStatus.OK);
    }
}
