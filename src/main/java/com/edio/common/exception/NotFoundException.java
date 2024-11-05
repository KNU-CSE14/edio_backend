package com.edio.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends BaseException{
    public NotFoundException(String serviceName, Long id){
        super(HttpStatus.NOT_FOUND, String.format("%s not found: %d", serviceName, id));
    }

    public NotFoundException(String serviceName, Object message){
        super(HttpStatus.NOT_FOUND, String.format("%s not found: %s", serviceName, message));
    }
}
