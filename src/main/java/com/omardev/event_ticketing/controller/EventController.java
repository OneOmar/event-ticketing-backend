package com.omardev.event_ticketing.controller;

import com.omardev.event_ticketing.dto.request.CreateEventRequest;
import com.omardev.event_ticketing.dto.request.UpdateEventRequest;
import com.omardev.event_ticketing.dto.response.EventResponse;
import com.omardev.event_ticketing.enums.EventStatus;
import com.omardev.event_ticketing.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Event management endpoints.
 */
@Tag(name = "Events", description = "Manage events (public & organizer operations)")
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    /**
     * Get all published events (public).
     */
    @Operation(
            summary = "Get published events",
            description = "Public endpoint. Returns published events with optional search."
    )
    @GetMapping
    public ResponseEntity<Page<EventResponse>> getPublishedEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return ResponseEntity.ok(
                eventService.getPublishedEvents(pageable, keyword)
        );
    }

    /**
     * Get event details by ID (public).
     */
    @Operation(
            summary = "Get event by ID",
            description = "Public endpoint. Returns event details."
    )
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable UUID id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    /**
     * Get current user's events.
     */
    @Operation(
            summary = "Get my events",
            description = "Requires authentication. Returns events created by the current user."
    )
    @GetMapping("/me")
    public ResponseEntity<Page<EventResponse>> getMyEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) EventStatus status
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                eventService.getMyEvents(pageable, status)
        );
    }

    /**
     * Create a new event.
     */
    @Operation(
            summary = "Create event",
            description = "Requires role: ORGANIZER"
    )
    @PreAuthorize("hasRole('ORGANIZER')")
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody CreateEventRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventService.createEvent(request));
    }

    /**
     * Publish an event.
     */
    @Operation(
            summary = "Publish event",
            description = "Requires role: ORGANIZER"
    )
    @PreAuthorize("hasRole('ORGANIZER')")
    @PatchMapping("/{id}/publish")
    public ResponseEntity<EventResponse> publishEvent(@PathVariable UUID id) {
        return ResponseEntity.ok(eventService.publishEvent(id));
    }

    /**
     * Cancel (unpublish) an event.
     */
    @Operation(
            summary = "Cancel event",
            description = "Requires role: ORGANIZER"
    )
    @PreAuthorize("hasRole('ORGANIZER')")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<EventResponse> cancelEvent(@PathVariable UUID id) {
        return ResponseEntity.ok(eventService.cancelEvent(id));
    }

    /**
     * Update event.
     */
    @Operation(
            summary = "Update event",
            description = "Requires role: ORGANIZER"
    )
    @PreAuthorize("hasRole('ORGANIZER')")
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEventRequest request
    ) {
        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }

    /**
     * Delete event.
     */
    @Operation(
            summary = "Delete event",
            description = "Requires role: ORGANIZER"
    )
    @PreAuthorize("hasRole('ORGANIZER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}