package com.omardev.event_ticketing.service;

import com.omardev.event_ticketing.dto.response.TicketResponse;

import java.util.UUID;

public interface TicketService {

    /**
     * Purchase a ticket for an event.
     */
    TicketResponse purchaseTicket(UUID eventId);
}