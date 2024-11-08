package com.edio.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException{

    private HttpStatus status;
    private boolean isSuccess;
    private String detailMessage;

    public BaseException(HttpStatus status, String message){
//        fillInStackTrace();
        super(message);  // RuntimeException의 메시지 필드 설정
        this.status = status;
        this.isSuccess = false;
        this.detailMessage = message;
    }
}
