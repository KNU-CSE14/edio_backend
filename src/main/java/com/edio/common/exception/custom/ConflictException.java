package com.edio.common.exception.custom;

import com.edio.common.domain.BaseEntity;
import com.edio.common.exception.base.BaseException;
import com.edio.common.exception.base.ErrorMessages;
import org.springframework.http.HttpStatus;

public class ConflictException extends BaseException {
    public <T extends BaseEntity> ConflictException(Class<T> entityClass, Long id) {
        super(HttpStatus.CONFLICT, String.format("%s " + ErrorMessages.CONFLICT + ": %d", entityClass.getSimpleName(), id));
    }

    public <T extends BaseEntity> ConflictException(Class<T> entityClass, Object message) {
        super(HttpStatus.CONFLICT, String.format("%s " + ErrorMessages.CONFLICT + ": %s", entityClass.getSimpleName(), message));
    }
}
