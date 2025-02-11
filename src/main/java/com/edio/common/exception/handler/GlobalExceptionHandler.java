package com.edio.common.exception.handler;

import com.edio.common.model.response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * BAD_REQUEST 동작(요청이 유효하지 않은 경우)
     *
     * @param ex
     * @return 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Error occurred: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * NoSuchElementException
     *
     * @param ex
     * @return 404
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex) {
        log.error("Error occurred: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * EntityNotFoundException (명시적 호출)
     *
     * @param ex
     * @return 404
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("Error occurred: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * DataIntegrityViolationException(CONFLICT)
     *
     * @param ex
     * @return 409
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Error occurred: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.CONFLICT);
    }

    /**
     * UnsupportedMediaTypeStatusException
     *
     * @param ex
     * @return 415
     */
    @ExceptionHandler(UnsupportedMediaTypeStatusException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaTypeStatusException(UnsupportedMediaTypeStatusException ex) {
        log.error("Error occurred: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * CONFLICT(리소스 충돌),
     * UNSUPPORTED_MEDIA_TYPE 동작(파일의 상태가 적당하지 않은 경우)
     *
     * @param ex
     * @return 409, 415, 422, 500
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        log.error("Error occurred: {}", ex.getMessage());

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex.getMessage().contains("File")) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
        } else if (ex.getMessage().contains("Conflict")) {
            status = HttpStatus.CONFLICT;
        }
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), status);
    }

    /**
     * INTERNAL_SERVER_ERROR
     *
     * @param ex
     * @return 500
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Error occurred: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
