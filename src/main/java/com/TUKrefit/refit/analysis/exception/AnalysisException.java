package com.TUKrefit.refit.analysis.exception;

import com.TUKrefit.refit.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public class AnalysisException extends ApiException {
    public AnalysisException(HttpStatus status, String code) {
        super(status, code);
    }

    public AnalysisException(HttpStatus status, String code, String message) {
        super(status, code, message);
    }
}
