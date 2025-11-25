package com.ticketnotify.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.ControllerAdvice;

import java.time.Instant;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String errorCode, String message) {
        ApiError body = new ApiError(
            false,
            errorCode,
            status.value(),
            message,
            Instant.now()
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiError> handleAppException(AppException ex) {
        log.warn("AppException: {} - {}", ex.getErrorCode(), ex.getMessage());
        
        return buildResponse(ex.getStatus(), ex.getErrorCode().getCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> String.format("Field '%s': %s", e.getField(), e.getDefaultMessage()))
                .findFirst()
                .orElse("Validation failed");

        log.warn("Validation Error: {}", errorMessage);

        return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_INPUT.getCode(), errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOther(Exception ex) {
        log.error("Unexpected Error: ", ex);

        return buildResponse(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            ErrorCode.INTERNAL_SERVER_ERROR.getCode(), 
            "Internal server error"
        );
    }
}