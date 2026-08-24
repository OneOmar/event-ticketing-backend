package com.omardev.event_ticketing.exception;

import java.util.UUID;

/**
 * Thrown when no tickets are available for an event.
 */
public class NoTicketsAvailableException extends ApiException {

    public NoTicketsAvailableException(UUID eventId) {
        super("No tickets available for event " + eventId);
    }
}