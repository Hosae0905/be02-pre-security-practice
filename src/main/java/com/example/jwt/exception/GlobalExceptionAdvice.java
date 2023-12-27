package com.example.jwt.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {


    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity applicationHandler(ApplicationException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(e.getErrorCode().name());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity applicationHandler(RuntimeException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
