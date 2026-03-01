package com.TUKrefit.refit.auth.exception;

import com.TUKrefit.refit.common.exception.ApiException;

public class AuthException extends ApiException {

    public AuthException(AuthErrorCode errorCode) {
        super(errorCode.status(), errorCode.code());
    }

    public AuthException(AuthErrorCode errorCode, String message) {
        super(errorCode.status(), errorCode.code(), message);
    }
}