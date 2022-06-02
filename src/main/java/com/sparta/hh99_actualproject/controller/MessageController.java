package com.sparta.hh99_actualproject.controller;

import com.fasterxml.jackson.core.JsonToken;
import com.sparta.hh99_actualproject.dto.MessageRequestDto;
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


//    Resolved [org.springframework.http.converter.HttpMessageNotReadableException: JSON parse error:
//    Cannot deserialize value of type `java.lang.String` from Array value (token `JsonToken.START_ARRAY`); nested exception is com.fasterxml.jackson.databind.exc.MismatchedInputException:
//    Cannot deserialize value of type `java.lang.String` from Array value (token `JsonToken.START_ARRAY`)<EOL>
//    at [Source: (org.springframework.util.StreamUtils$NonClosingInputStream); line: 1, column: 122]
//    (through reference chain: com.sparta.hh99_actualproject.dto.MessageDto$MessageRequestDto["resUserNickName"])]

}
