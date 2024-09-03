package com.edio.common.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException{

    private boolean isSuccess;
    private String detailMessage;

    public BaseException(String message){
        fillInStackTrace();
        this.isSuccess = false;
        this.detailMessage = message;
    }
}
