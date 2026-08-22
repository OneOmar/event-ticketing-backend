package com.omardev.event_ticketing.exception;

/**
 * Thrown when event dates are invalid.
 */
public class InvalidEventDatesException extends RuntimeException {

    public InvalidEventDatesException() {
        super("End date must be after start date");
    }
}