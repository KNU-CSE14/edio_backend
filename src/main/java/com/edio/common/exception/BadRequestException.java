package com.edio.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BadRequestException extends BaseException {
    public <T> BadRequestException(Class<T> entityClass, Long id) {
        super(HttpStatus.BAD_REQUEST, String.format("%s bad request: %d", entityClass.getSimpleName(), id));
    }

    public <T> BadRequestException(Class<T> entityClass, Object message) {
        super(HttpStatus.BAD_REQUEST, String.format("%s bad request: %s", entityClass.getSimpleName(), message));
    }
}
