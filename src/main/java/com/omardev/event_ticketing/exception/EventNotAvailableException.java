package com.omardev.event_ticketing.exception;

import java.util.UUID;

/**
 * Thrown when an event is not available for ticket purchase.
 */
public class EventNotAvailableException extends ApiException {

    public EventNotAvailableException(UUID eventId) {
        super("Event " + eventId + " is not available for purchase");
    }
}