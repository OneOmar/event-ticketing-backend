package com.omardev.event_ticketing.exception;

import java.util.UUID;

public class TicketNotFoundException extends ApiException {

    public TicketNotFoundException(UUID id) {
        super("Ticket not found with id: " + id);
    }
}