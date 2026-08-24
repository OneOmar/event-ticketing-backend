package com.omardev.event_ticketing.service;

import com.omardev.event_ticketing.dto.request.PurchaseTicketRequest;
import com.omardev.event_ticketing.dto.response.TicketResponse;

public interface TicketService {

    /**
     * Purchase a ticket for an event.
     */

    TicketResponse purchaseTicket(PurchaseTicketRequest request);
}