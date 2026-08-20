package com.omardev.event_ticketing.exception;

import java.util.UUID;

public class EventNotFoundException extends ApiException {

    public EventNotFoundException(UUID eventId) {
        super("Event not found with ID: " + eventId);
    }
}