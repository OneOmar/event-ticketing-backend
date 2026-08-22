package com.omardev.event_ticketing.service;

import com.omardev.event_ticketing.dto.request.CreateEventRequest;
import com.omardev.event_ticketing.dto.response.EventResponse;
import com.omardev.event_ticketing.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface EventService {

    /**
     * Create a new event.
     */
    EventResponse createEvent(CreateEventRequest request);

    /**
     * Retrieve all events with pagination.
     */
    Page<EventResponse> getAllEvents(Pageable pageable);

    /**
     * Retrieve events created by the authenticated user.
     */
    List<EventResponse> getMyEvents();

}