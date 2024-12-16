package com.edio.common.exception;

import org.springframework.http.HttpStatus;

public class UnprocessableException extends BaseException {
    public <T> UnprocessableException(Class<T> entityClass, Long id) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, String.format("%s UnProcessable entity: %d", entityClass.getSimpleName(), id));
    }

    public <T> UnprocessableException(Class<T> entityClass, Object message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, String.format("%s UnProcessable entity: %s", entityClass.getSimpleName(), message));
    }
}
