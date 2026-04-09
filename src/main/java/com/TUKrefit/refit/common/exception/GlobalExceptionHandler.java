package com.TUKrefit.refit.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handle(ApiException e) {
        return ResponseEntity.status(e.getStatus())
                .body(Map.of("error", e.getCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException e) {
        // 상세 검증 메시지는 노출하지 않고 공통 코드만 반환
        return ResponseEntity.badRequest()
                .body(Map.of("error", "INVALID_REQUEST"));
    }
}
