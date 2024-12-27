package com.edio.common.exception.custom;

import com.edio.common.domain.BaseEntity;
import com.edio.common.exception.base.BaseException;
import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseException {
    public <T extends BaseEntity> BadRequestException(Class<T> entityClass, Long id) {
        super(HttpStatus.BAD_REQUEST, String.format("%s bad request: %d", entityClass.getSimpleName(), id));
    }

    public <T extends BaseEntity> BadRequestException(Class<T> entityClass, Object message) {
        super(HttpStatus.BAD_REQUEST, String.format("%s bad request: %s", entityClass.getSimpleName(), message));
    }
}
