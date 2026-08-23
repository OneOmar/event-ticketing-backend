package com.omardev.event_ticketing.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response returned after ticket purchase.
 */
public record TicketResponse(

        UUID ticketId,      // Ticket unique ID
        UUID eventId,       // Related event
        String qrCode,      // QR token (for now, simple string)

        LocalDateTime createdAt

) {}