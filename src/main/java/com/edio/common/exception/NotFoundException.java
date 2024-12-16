package com.edio.common.exception;

import com.edio.common.domain.BaseEntity;
import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException {
    public <T extends BaseEntity> NotFoundException(Class<T> entityClass, Long id) {
        super(HttpStatus.NOT_FOUND, String.format("%s not found: %d", entityClass.getSimpleName(), id));
    }

    public <T extends BaseEntity> NotFoundException(Class<T> entityClass, Object message) {
        super(HttpStatus.NOT_FOUND, String.format("%s not found: %s", entityClass.getSimpleName(), message));
    }
}
