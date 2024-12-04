package com.edio.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {

    private final HttpStatus status;
    private final boolean isSuccess;
    private final String detailMessage;

    public BaseException(HttpStatus status, String message) {
        super(message);  // RuntimeException의 메시지 필드 설정
        this.status = status;
        this.isSuccess = false;
        this.detailMessage = message;
    }
}
