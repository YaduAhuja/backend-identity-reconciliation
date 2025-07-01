package com.example.demo.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    ResponseEntity<String> handleException(Exception exception) {
        log.error("Unhandled Exception occurred", exception);
        return ResponseEntity.internalServerError().body("Something went wrong");
    }
}
