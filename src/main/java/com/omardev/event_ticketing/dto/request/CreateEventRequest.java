package com.omardev.event_ticketing.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

public record CreateEventRequest(

        @NotBlank
        String title,

        String description,

        @NotBlank
        String location,

        @NotBlank
        String bannerUrl,

        @NotNull
        @Future
        LocalDateTime startDate,

        @NotNull
        @Future
        LocalDateTime endDate,

        @NotNull
        @Positive
        @Min(1)
        Integer capacity,

        @NotEmpty
        List<@Valid CreateTicketTypeRequest> ticketTypes

) {
}