package com.omardev.event_ticketing.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PurchaseTicketRequest(

        @NotNull(message = "Event ID is required")
        UUID eventId,

        @NotNull(message = "Ticket type ID is required")
        UUID ticketTypeId,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
) {}