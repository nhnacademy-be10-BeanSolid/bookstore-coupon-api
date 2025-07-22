package com.nhnacademy.common.handler;

import com.nhnacademy.common.exception.CustomHttpException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomHttpException.class)
    public ResponseEntity<ErrorMessage> handleException(CustomHttpException e, HttpServletRequest request) {

        int statusCode = e.getCustomHttpStatus().getCode();
        String reasonPhrase = e.getCustomHttpStatus().name();

        ErrorMessage errorMessage = new ErrorMessage(
                statusCode,
                reasonPhrase,
                request.getRequestURI(),
                e.getMessage()
        );
        return ResponseEntity.status(statusCode).body(errorMessage);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {

        int statusCode = HttpStatus.BAD_REQUEST.value();
        String reasonPhrase = HttpStatus.BAD_REQUEST.getReasonPhrase();
        ErrorMessage errorMessage = new ErrorMessage(
                statusCode,
                reasonPhrase,
                request.getRequestURI(),
                ex.getMessage()
        );
        return ResponseEntity.status(statusCode).body(errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(Exception e, HttpServletRequest request) {
        CustomHttpException.CustomHttpStatus status = CustomHttpException.CustomHttpStatus.INTERNAL_SERVER_ERROR;
        ErrorMessage errorMessage = new ErrorMessage(
                status.getCode(),
                status.name(),
                request.getRequestURI(),
                e.getMessage()
        );
        return ResponseEntity.status(status.getCode()).body(errorMessage);
    }
}