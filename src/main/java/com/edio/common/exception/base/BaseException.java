package com.edio.common.exception.base;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {

    private final HttpStatus status;
    private final boolean isSuccess;

    public BaseException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.isSuccess = false;
    }
}
