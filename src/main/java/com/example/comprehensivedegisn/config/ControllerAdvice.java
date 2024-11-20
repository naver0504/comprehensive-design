package com.example.comprehensivedegisn.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RuntimeException> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e);
    }
}
