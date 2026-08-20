package com.omardev.event_ticketing.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

public record CreateEventRequest(

        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotBlank(message = "Location is required")
        String location,

        @NotBlank(message = "Banner URL is required")
        String bannerUrl,

        @NotNull(message = "Start date is required")
        @Future(message = "Start date must be in the future")
        LocalDateTime startDate,

        @NotNull(message = "End date is required")
        @Future(message = "End date must be in the future")
        LocalDateTime endDate,

        @NotNull(message = "Capacity is required")
        @Positive(message = "Capacity must be greater than 0")
        @Min(value = 1, message = "Capacity must be at least 1")
        Integer capacity,

        @NotEmpty(message = "At least one ticket type is required")
        List<@Valid CreateTicketTypeRequest> ticketTypes

) {}