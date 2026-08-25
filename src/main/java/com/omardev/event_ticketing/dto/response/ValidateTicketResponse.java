package com.omardev.event_ticketing.dto.response;

public record ValidateTicketResponse(
        boolean valid,
        String message,
        String ticketCode,
        String eventTitle
) {}