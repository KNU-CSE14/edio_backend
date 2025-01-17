package com.edio.common.exception.handler;

import com.edio.common.exception.base.BaseException;
import com.edio.common.exception.base.ErrorMessages;
import com.edio.common.model.response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 공통 BaseException
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        log.error("Error occurred: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(ex.isSuccess(), ex.getMessage());
        return new ResponseEntity<>(response, ex.getStatus());
    }

    /**
     * BAD_REQUEST 동작(요청이 유효하지 않은 경우)
     *
     * @param ex
     * @return 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Error occurred: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(false, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        log.error("Error occurred: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(false, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * FORBIDDEN 동작(권한)
     *
     * @param ex
     * @return 403
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Error occurred: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(false, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * NOT_FOUND 동작(요청 자체는 유효하지만 결과가 없음)
     *
     * @param ex
     * @return 404
     */
    @ExceptionHandler({NoSuchElementException.class, EntityNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException ex) {
        log.error("Error occurred: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(false, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
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
        ErrorResponse response = new ErrorResponse(false, ErrorMessages.CONFLICT.format(ex.getMessage()));
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * UnsupportedMediaTypeStatusException(
     *
     * @param ex
     * @return 415
     */
    @ExceptionHandler(UnsupportedMediaTypeStatusException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaTypeStatusException(UnsupportedMediaTypeStatusException ex) {
        log.error("Error occurred: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(false, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
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

        ErrorResponse response = new ErrorResponse(false, ex.getMessage());
        return new ResponseEntity<>(response, status);
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
        ErrorResponse response = new ErrorResponse(false, ErrorMessages.INTERNAL_SERVER_ERROR.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
