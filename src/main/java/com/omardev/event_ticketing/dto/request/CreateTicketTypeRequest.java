package com.omardev.event_ticketing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateTicketTypeRequest(

        @NotBlank(message = "Ticket type name is required")
        String name,

        String description,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than 0")
        BigDecimal price,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than 0")
        Integer quantity

) {}