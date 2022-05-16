package com.sparta.hh99_actualproject.controller;

import com.sparta.hh99_actualproject.dto.MessageDto.MessageRequestDto;
import com.sparta.hh99_actualproject.exception.PrivateResponseBody;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/message")
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/detail/{messageId}")
    ResponseEntity<PrivateResponseBody> getMessageDetail(@PathVariable Long messageId){

        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK , messageService.getMessageDetail(messageId)) , HttpStatus.OK);
    }

    @PostMapping(value = "/request")
    ResponseEntity<PrivateResponseBody> sendMessage(@RequestBody MessageRequestDto messageRequestDto) {
        messageService.sendMessage(messageRequestDto);
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK , null) , HttpStatus.OK);
    }
}
