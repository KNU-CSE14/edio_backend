package com.edio.common.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends BaseException {
    public <T> AuthenticationException(Class<T> entityClass, Long id) {
        super(HttpStatus.UNAUTHORIZED, String.format("%s unAuthorized: %d", entityClass.getSimpleName(), id));
    }

    public <T> AuthenticationException(Class<T> entityClass, Object message) {
        super(HttpStatus.UNAUTHORIZED, String.format("%s unAuthorized: %s", entityClass.getSimpleName(), message));
    }
}
