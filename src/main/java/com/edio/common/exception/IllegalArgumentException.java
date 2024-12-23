package com.edio.common.exception;

import org.springframework.http.HttpStatus;

public class IllegalArgumentException extends BaseException {
    public <T> IllegalArgumentException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public <T> IllegalArgumentException(String message, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, cause.getMessage());
    }
}
