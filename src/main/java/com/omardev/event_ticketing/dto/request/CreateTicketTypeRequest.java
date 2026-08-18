package com.omardev.event_ticketing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateTicketTypeRequest(
        @NotBlank
        String name,

        String description,

        @NotNull
        @Positive
        BigDecimal price,

        @NotNull
        @Positive
        Integer quantity

) {}
