package com.sparta.hh99_actualproject.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestApiExceptionHandler {

    @ExceptionHandler(value = { PrivateException.class })
    public ResponseEntity<Object> handleApiRequestException(PrivateException ex) {
        String errCode = ex.getStatusCode().getStatusCode();
        String errMSG = ex.getStatusCode().getStatusMsg();
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto();
        exceptionResponseDto.setStatusCode(errCode);
        exceptionResponseDto.setStatusMsg(errMSG);

        System.out.println("ERR :" + errCode + " , " + errMSG);  //Log용

        return new ResponseEntity(
                exceptionResponseDto,
                ex.getStatusCode().getHttpStatus()
        );
    }
}
