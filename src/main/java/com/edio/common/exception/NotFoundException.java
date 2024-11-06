package com.edio.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends BaseException{
    public <T> NotFoundException(Class<T> entityClass, Long id){
        super(HttpStatus.NOT_FOUND, String.format("%s not found: %d", entityClass.getSimpleName(), id));
    }

    public <T> NotFoundException(Class<T> entityClass, Object message){
        super(HttpStatus.NOT_FOUND, String.format("%s not found: %s", entityClass.getSimpleName(), message));
    }
}
