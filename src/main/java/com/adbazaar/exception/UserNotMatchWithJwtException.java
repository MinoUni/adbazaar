package com.adbazaar.exception;

public class UserNotMatchWithJwtException extends RuntimeException {

    public UserNotMatchWithJwtException(String message) {
        super(message);
    }
}
