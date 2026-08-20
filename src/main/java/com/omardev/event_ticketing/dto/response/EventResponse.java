package com.omardev.event_ticketing.dto.response;

import com.omardev.event_ticketing.enums.EventStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record EventResponse(

        UUID id,
        String title,
        String description,
        String location,
        String bannerUrl,

        LocalDateTime startDate,
        LocalDateTime endDate,

        Integer capacity,
        Integer availableTickets,

        EventStatus status,

        UUID organizerId,
        String organizerName,

        List<TicketTypeResponse> ticketTypes,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {}