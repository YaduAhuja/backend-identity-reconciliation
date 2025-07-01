package com.example.demo.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        log.info("Handling {}", exception.getClass().getName(), exception);
        return ResponseEntity.badRequest().body("Invalid Request Method");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException exception) {
        log.info("Handling {}", exception.getClass().getName(), exception);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<String> handleException(Exception exception) {
        log.error("Unhandled Exception occurred", exception);
        return ResponseEntity.internalServerError().body("Something went wrong");
    }
}
