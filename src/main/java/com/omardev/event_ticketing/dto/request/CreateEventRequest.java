package com.omardev.event_ticketing.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

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
        Integer capacity

) {
}