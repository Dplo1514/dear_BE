package com.sparta.hh99_clonecoding.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestApiExceptionHandler {

    @ExceptionHandler(value = { PrivateException.class })
    public ResponseEntity<Object> handleApiRequestException(PrivateException ex) {
        String errCode = ex.getCode().getCode();
        String errMSG = ex.getCode().getMsg();
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto();
        exceptionResponseDto.setCode(errCode);
        exceptionResponseDto.setMsg(errMSG);

        System.out.println("ERR :" + errCode + " , " + errMSG);  //Log용

        return new ResponseEntity(
                exceptionResponseDto,
                ex.getCode().getHttpStatus()
        );
    }
}
