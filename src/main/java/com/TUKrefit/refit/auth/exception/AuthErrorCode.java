package com.TUKrefit.refit.auth.exception;

import org.springframework.http.HttpStatus;

public enum AuthErrorCode {
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"),
    AUTH_LOG_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH_LOG_NOT_FOUND"),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_INVALID");

    private final HttpStatus status;
    private final String code;

    AuthErrorCode(HttpStatus status, String code) {
        this.status = status;
        this.code = code;
    }

    public HttpStatus status() { return status; }
    public String code() { return code; }
}