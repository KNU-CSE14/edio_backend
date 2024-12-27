package com.edio.common.exception.custom;

import com.edio.common.exception.base.BaseException;
import org.springframework.http.HttpStatus;

public class IllegalArgumentException extends BaseException {
    public <T> IllegalArgumentException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public <T> IllegalArgumentException(Throwable cause) {
        super(HttpStatus.BAD_REQUEST, cause.getMessage());
    }
}
