package com.edio.common.exception;

import org.springframework.http.HttpStatus;

public class InternalServerException extends BaseException {
    public <T> InternalServerException(Class<T> entityClass, Long id) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, String.format("%s internal server error: %d", entityClass.getSimpleName(), id));
    }

    public <T> InternalServerException(Class<T> entityClass, Object message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, String.format("%s internal server error: %s", entityClass.getSimpleName(), message));
    }
}
