package com.omardev.event_ticketing.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}