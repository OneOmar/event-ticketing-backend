package com.omardev.event_ticketing.service;

import com.omardev.event_ticketing.dto.request.PurchaseTicketRequest;
import com.omardev.event_ticketing.dto.response.TicketResponse;

import java.util.List;

public interface TicketService {

    /**
     * Purchase multiple tickets for an event.
     */
    List<TicketResponse> purchaseTicket(PurchaseTicketRequest request);

    /**
     * Retrieve all tickets for the current user.
     */
    List<TicketResponse> getMyTickets();
}