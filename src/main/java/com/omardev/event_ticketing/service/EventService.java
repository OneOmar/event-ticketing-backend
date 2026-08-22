package com.omardev.event_ticketing.service;

import com.omardev.event_ticketing.dto.request.CreateEventRequest;
import com.omardev.event_ticketing.dto.request.UpdateEventRequest;
import com.omardev.event_ticketing.dto.response.EventResponse;
import com.omardev.event_ticketing.enums.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;


public interface EventService {

    /**
     * Retrieve all events with pagination.
     */
    Page<EventResponse> getAllEvents(Pageable pageable);

    /**
     * Retrieve published events with optional search.
     */
    Page<EventResponse> getPublishedEvents(
            Pageable pageable,
            String keyword
    );

    /**
     * Retrieve event by its ID.
     */
    EventResponse getEventById(UUID eventId);

    /**
     * Retrieve authenticated user's events with pagination and optional filtering.
     */
    Page<EventResponse> getMyEvents(
            Pageable pageable,
            EventStatus status
    );

    /**
     * Create a new event.
     */
    EventResponse createEvent(CreateEventRequest request);

    /**
     * Update an existing event.
     */
    EventResponse updateEvent(UUID eventId, UpdateEventRequest request);

    /**
     * Delete an event by ID.
     */
    void deleteEvent(UUID eventId);
}