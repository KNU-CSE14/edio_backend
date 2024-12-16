package com.edio.common.exception;

import com.edio.common.domain.BaseEntity;
import org.springframework.http.HttpStatus;

public class ConflictException extends BaseException {
    public <T extends BaseEntity> ConflictException(Class<T> entityClass, Long id) {
        super(HttpStatus.CONFLICT, String.format("%s not found: %d", entityClass.getSimpleName(), id));
    }

    public <T extends BaseEntity> ConflictException(Class<T> entityClass, Object message) {
        super(HttpStatus.CONFLICT, String.format("%s not found: %s", entityClass.getSimpleName(), message));
    }
}
