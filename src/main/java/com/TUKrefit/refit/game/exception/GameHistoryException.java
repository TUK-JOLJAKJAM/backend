package com.TUKrefit.refit.game.exception;

import com.TUKrefit.refit.common.exception.ApiException;

public class GameHistoryException extends ApiException {

    public GameHistoryException(GameHistoryErrorCode errorCode) {
        super(errorCode.status(), errorCode.code());
    }

    public GameHistoryException(GameHistoryErrorCode errorCode, String message) {
        super(errorCode.status(), errorCode.code(), message);
    }
}
