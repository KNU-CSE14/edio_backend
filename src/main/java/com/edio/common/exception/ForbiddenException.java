package com.edio.common.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseException {
    public <T> ForbiddenException(Class<T> entityClass, Long id) {
        super(HttpStatus.FORBIDDEN, String.format("%s forbidden: %d", entityClass.getSimpleName(), id));
    }

    public <T> ForbiddenException(Class<T> entityClass, Object message) {
        super(HttpStatus.FORBIDDEN, String.format("%s forbidden: %s", entityClass.getSimpleName(), message));
    }
}
