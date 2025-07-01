package com.example.demo.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        log.info("Handling {}", exception.getClass().getName(), exception);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("Invalid Request Method");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException exception) {
        log.info("Handling {}", exception.getClass().getName(), exception);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.info("Handling {}", exception.getClass().getName(), exception);
        return ResponseEntity.badRequest().body("Unable to read request body.");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<String> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
        log.info("Handling {}", exception.getClass().getName(), exception);
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Content-Type not supported");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, List<String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.info("Handling {}", exception.getClass().getName(), exception);
        Map<String, List<String>> responseErrors = new HashMap<>();
        BindingResult bindingResult = exception.getBindingResult();
        List<FieldError> bindingErrors = bindingResult.getFieldErrors();
        for (FieldError error : bindingErrors) {
            List<String> fieldErrors = responseErrors.getOrDefault(error.getField(), new ArrayList<>(1));
            fieldErrors.add(error.getDefaultMessage());
            responseErrors.put(error.getField(), fieldErrors);
        }
        return ResponseEntity.badRequest().body(responseErrors);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<String> handleException(Exception exception) {
        log.error("Unhandled Exception occurred", exception);
        return ResponseEntity.internalServerError().body("Something went wrong");
    }
}
