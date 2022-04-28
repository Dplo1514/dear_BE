package com.sparta.hh99_actualproject.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExceptionResponseDto {
    private String statusCode;
    private String statusMsg;
    private Object data;

    public ExceptionResponseDto(StatusCode statusCode) {
        this.statusCode = statusCode.getStatusCode();
        this.statusMsg = statusCode.getStatusMsg();
    }
    public ExceptionResponseDto(StatusCode statusCode, Object data){
        this.statusCode = statusCode.getStatusCode();
        this.statusMsg = statusCode.getStatusMsg();
        this.data = data;
    }
}