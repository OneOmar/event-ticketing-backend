package com.omardev.event_ticketing.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;


/**
 * Request to update an event (partial update).
 * Only provided fields will be updated.
 */
public record UpdateEventRequest(

        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
        String title,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @Size(min = 3, max = 100, message = "Location must be between 3 and 100 characters")
        String location,

        @Pattern(
                regexp = "^(http|https)://.*$",
                message = "Banner URL must be a valid URL"
        )
        String bannerUrl,

        @Future(message = "Start date must be in the future")
        LocalDateTime startDate,

        @Future(message = "End date must be in the future")
        LocalDateTime endDate,

        @Positive(message = "Capacity must be greater than 0")
        @Min(value = 1, message = "Capacity must be at least 1")
        Integer capacity

) {}