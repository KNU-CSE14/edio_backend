package com.edio.common.exception.custom;

import com.edio.common.exception.base.BaseException;
import org.springframework.http.HttpStatus;

public class InternalServerException extends BaseException {
    public <T> InternalServerException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public <T> InternalServerException(Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, cause.getMessage());
    }
}
