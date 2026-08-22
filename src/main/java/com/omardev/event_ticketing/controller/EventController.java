package com.omardev.event_ticketing.controller;

import com.omardev.event_ticketing.dto.request.CreateEventRequest;
import com.omardev.event_ticketing.dto.request.UpdateEventRequest;
import com.omardev.event_ticketing.dto.response.EventResponse;
import com.omardev.event_ticketing.enums.EventStatus;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

//    /**
//     * Get all events with pagination.
//     */
//    @GetMapping
//    public ResponseEntity<Page<EventResponse>> getAllEvents(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//
//        // Build pagination object
//        Pageable pageable = PageRequest.of(page, size);
//
//        // Delegate to service layer
//        Page<EventResponse> response = eventService.getAllEvents(pageable);
//
//        // Return 200 OK with paginated result
//        return ResponseEntity.ok(response);
//    }


    /**
     * Get published events with optional search.
     */
    @GetMapping
    public ResponseEntity<Page<EventResponse>> getPublishedEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword
    ) {

        // Build pagination
        Pageable pageable = PageRequest.of(page, size);

        // Call service
        Page<EventResponse> response =
                eventService.getPublishedEvents(pageable, keyword);

        // Return 200 OK
        return ResponseEntity.ok(response);
    }

    /**
     * Get event details by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(
            @PathVariable UUID id
    ) {

        // Call service
        EventResponse response = eventService.getEventById(id);

        // Return 200 OK
        return ResponseEntity.ok(response);
    }


    /**
     * Get authenticated user's events with pagination and optional status filter.
     */
    @GetMapping("/me")
    public ResponseEntity<Page<EventResponse>> getMyEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) EventStatus status // optional filter
    ) {

        // Build pagination object
        Pageable pageable = PageRequest.of(page, size);

        // Call service
        Page<EventResponse> response =
                eventService.getMyEvents(pageable, status);

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

    /**
     * Update event by ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEventRequest request
    ) {

        EventResponse response = eventService.updateEvent(id, request);

        return ResponseEntity.ok(response);
    }

    /**
     * Delete event by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {

        // Call service
        eventService.deleteEvent(id);

        // Return 204 No Content (standard for delete)
        return ResponseEntity.noContent().build();
    }
}