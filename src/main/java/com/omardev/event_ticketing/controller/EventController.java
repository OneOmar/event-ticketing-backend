package com.omardev.event_ticketing.controller;

import com.omardev.event_ticketing.dto.request.CreateEventRequest;
import com.omardev.event_ticketing.dto.response.EventResponse;
import com.omardev.event_ticketing.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    /**
     * Get all events with pagination.
     */
    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        // Build pagination object
        Pageable pageable = PageRequest.of(page, size);

        // Delegate to service layer
        Page<EventResponse> response = eventService.getAllEvents(pageable);

        // Return 200 OK with paginated result
        return ResponseEntity.ok(response);
    }


    /**
     * Get events created by the authenticated user.
     */
    @GetMapping("/me")
    public ResponseEntity<List<EventResponse>> getMyEvents() {

        // Call service to get user's events
        List<EventResponse> response = eventService.getMyEvents();

        // Return 200 OK
        return ResponseEntity.ok(response);
    }


    /**
     * Create a new event
     */
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody CreateEventRequest request
    ) {

        EventResponse response = eventService.createEvent(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}