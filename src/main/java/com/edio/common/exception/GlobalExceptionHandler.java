package com.edio.common.exception;

import com.edio.common.model.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex){
		ErrorResponse response = new ErrorResponse(ex.isSuccess(), ex.getDetailMessage());

        log.error("Error occurred: {}", ex.getDetailMessage());
        
		return new ResponseEntity<>(response, ex.getStatus());
	}
}
