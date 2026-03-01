package com.TUKrefit.refit.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handle(ApiException e) {
        return ResponseEntity.status(e.getStatus())
                .body(Map.of("error", e.getCode()));
    }
}