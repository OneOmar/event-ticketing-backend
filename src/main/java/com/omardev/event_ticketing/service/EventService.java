package com.omardev.event_ticketing.service;

import com.omardev.event_ticketing.dto.request.CreateEventRequest;
import com.omardev.event_ticketing.dto.response.EventResponse;
import com.omardev.event_ticketing.entity.Event;

import java.util.List;
import java.util.UUID;

public interface EventService {

    /**
     * Create a new event.
     */
    EventResponse createEvent(CreateEventRequest request);
}