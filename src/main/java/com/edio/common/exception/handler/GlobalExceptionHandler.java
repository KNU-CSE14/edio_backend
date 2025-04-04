package com.edio.common.exception.handler;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_OCCURRED = "Error occurred: {}";

    /**
     * BAD_REQUEST 동작(요청이 유효하지 않은 경우)
     *
     * @param ex
     * @return 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error(ERROR_OCCURRED, ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * FORBIDDEN 동작(권한이 없는 경우)
     *
     * @param ex
     * @return 403
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    /**
     * NoSuchElementException, EntityNotFoundException
     *
     * @param ex
     * @return 404
     */
    @ExceptionHandler({NoSuchElementException.class, EntityNotFoundException.class})
    public ResponseEntity<String> handleNotFoundExceptions(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * DataIntegrityViolationException(CONFLICT)
     *
     * @param ex
     * @return 409
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error(ERROR_OCCURRED, ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * UnsupportedMediaTypeStatusException, UnsupportedOperationException
     *
     * @param ex
     * @return 415
     */
    @ExceptionHandler({UnsupportedMediaTypeStatusException.class, UnsupportedOperationException.class})
    public ResponseEntity<String> handleUnsupportedMediaTypeException(RuntimeException ex) {
        log.error(ERROR_OCCURRED, ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * CONFLICT(리소스 충돌),
     * UNSUPPORTED_MEDIA_TYPE 동작(파일의 상태가 적당하지 않은 경우)
     *
     * @param ex
     * @return 409, 415, 422, 500
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        log.error(ERROR_OCCURRED, ex.getMessage());

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex.getMessage().contains("File")) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
        } else if (ex.getMessage().contains("Conflict")) {
            status = HttpStatus.CONFLICT;
        }
        return new ResponseEntity<>(ex.getMessage(), status);
    }

    /**
     * INTERNAL_SERVER_ERROR
     *
     * @param ex
     * @return 500
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        log.error(ERROR_OCCURRED, ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
