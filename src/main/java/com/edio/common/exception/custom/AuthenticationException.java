package com.edio.common.exception.custom;

import com.edio.common.exception.base.BaseException;
import org.springframework.http.HttpStatus;

public class AuthenticationException extends BaseException {
    public <T> AuthenticationException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }

    public <T> AuthenticationException(Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, cause.getMessage());
    }
}
