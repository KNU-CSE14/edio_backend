package com.edio.common.exception;

import com.edio.common.model.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex){
		ErrorResponse response = new ErrorResponse(ex.isSuccess(), ex.getDetailMessage());

        logger.error("Error occurred: {}", ex.getDetailMessage());
        
		return new ResponseEntity<>(response, ex.getStatus());
	}

}
