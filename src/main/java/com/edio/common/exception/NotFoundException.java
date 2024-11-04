package com.edio.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends RuntimeException{
    private final HttpStatus status;
    private final boolean isSuccess;
    private final String detailMessage;

    public NotFoundException(String serviceName, Long id){
        super(String.format("%s not found: %d", serviceName, id));
        fillInStackTrace();
        this.status = HttpStatus.NOT_FOUND;
        this.isSuccess = false;
        this.detailMessage = String.format("%s not found: %d", serviceName, id);
        this.printDetailMessage();
    }

    public NotFoundException(String serviceName, Object message){
        super();
        fillInStackTrace();
        this.status = HttpStatus.NOT_FOUND;
        this.isSuccess = false;
        this.detailMessage = String.format("%s not found: %s", serviceName, message);
        this.printDetailMessage();
    }

    public void printDetailMessage() {
        System.out.println(this.detailMessage);
    }
}
