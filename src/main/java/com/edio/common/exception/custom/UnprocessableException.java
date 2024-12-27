package com.edio.common.exception.custom;

import com.edio.common.domain.BaseEntity;
import com.edio.common.exception.base.BaseException;
import org.springframework.http.HttpStatus;

public class UnprocessableException extends BaseException {
    public <T> UnprocessableException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }

    public <T> UnprocessableException(Throwable cause) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, cause.getMessage());
    }

    public <T extends BaseEntity> UnprocessableException(Class<T> entityClass, Long id) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, String.format("%s UnProcessable entity: %d", entityClass.getSimpleName(), id));
    }

    public <T extends BaseEntity> UnprocessableException(Class<T> entityClass, Object message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, String.format("%s UnProcessable entity: %s", entityClass.getSimpleName(), message));
    }
}
