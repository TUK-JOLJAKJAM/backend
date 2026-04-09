package com.TUKrefit.refit.game.exception;

import org.springframework.http.HttpStatus;

public enum GameHistoryErrorCode {
    INVALID_GAME_HISTORY(HttpStatus.BAD_REQUEST, "INVALID_GAME_HISTORY"),
    GAME_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "GAME_HISTORY_NOT_FOUND");

    private final HttpStatus status;
    private final String code;

    GameHistoryErrorCode(HttpStatus status, String code) {
        this.status = status;
        this.code = code;
    }

    public HttpStatus status() { return status; }
    public String code() { return code; }
}
