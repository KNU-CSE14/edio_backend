package com.edio.common.exception.custom;

import com.edio.common.domain.BaseEntity;
import com.edio.common.exception.base.BaseException;
import com.edio.common.exception.base.ErrorMessages;
import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException {
    public <T extends BaseEntity> NotFoundException(Class<T> entityClass, Long id) {
        super(HttpStatus.NOT_FOUND, String.format("%s " + ErrorMessages.NOT_FOUND + ": %d", entityClass.getSimpleName(), id));
    }

    public <T extends BaseEntity> NotFoundException(Class<T> entityClass, Object message) {
        super(HttpStatus.NOT_FOUND, String.format("%s " + ErrorMessages.NOT_FOUND + ": %s", entityClass.getSimpleName(), message));
    }
}
