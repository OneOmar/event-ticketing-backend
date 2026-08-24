package com.omardev.event_ticketing.dto.request;

import java.util.UUID;

public record PurchaseTicketRequest(
        UUID eventId,
        UUID ticketTypeId
) {}