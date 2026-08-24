package com.omardev.event_ticketing.exception;

import java.util.UUID;

public class TicketTypeNotFoundException extends ApiException {
    public TicketTypeNotFoundException(UUID id) {
        super("Ticket type not found with id: " + id);
    }
}