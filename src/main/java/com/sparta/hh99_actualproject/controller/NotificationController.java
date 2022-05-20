package com.sparta.hh99_actualproject.controller;

import com.sparta.hh99_actualproject.dto.NotificationResponseDto;
import com.sparta.hh99_actualproject.exception.PrivateResponseBody;
import com.sparta.hh99_actualproject.exception.StatusCode;
import com.sparta.hh99_actualproject.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class NotificationController {
    private NotificationService notificationService;

    //미수신 알람 수 return
    @GetMapping("/alarm")
    public ResponseEntity<PrivateResponseBody> getUnReadAlarmNum (){
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK, notificationService.getUnReadAlarmNum()), HttpStatus.OK);
    }

    //미수신 알람 모두 Get
    @GetMapping("/alarm/all")
    public ResponseEntity<PrivateResponseBody> getAlarmAllList(){
        List<NotificationResponseDto> notificationResponseDtoList = notificationService.getAlarmAllList();
        return new ResponseEntity<>(new PrivateResponseBody(StatusCode.OK, notificationResponseDtoList), HttpStatus.OK);
    }
}
