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
        PrivateResponseBody privateResponseBody = new PrivateResponseBody();
        privateResponseBody.setStatusCode(errCode);
        privateResponseBody.setStatusMsg(errMSG);

        System.out.println("ERR :" + errCode + " , " + errMSG);  //Log용

        return new ResponseEntity(
                privateResponseBody,
                ex.getStatusCode().getHttpStatus()
        );
    }
}
