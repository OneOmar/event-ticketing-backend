package com.omardev.event_ticketing.exception;

import java.util.UUID;

public class TicketTypeSoldOutException extends ApiException {
    public TicketTypeSoldOutException(UUID id) {
        super("No tickets available for ticket type: " + id);
    }
}