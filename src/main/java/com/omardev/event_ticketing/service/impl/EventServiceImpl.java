package com.omardev.event_ticketing.service.impl;

import com.omardev.event_ticketing.dto.request.CreateEventRequest;
import com.omardev.event_ticketing.dto.request.UpdateEventRequest;
import com.omardev.event_ticketing.dto.response.EventResponse;
import com.omardev.event_ticketing.entity.Event;
import com.omardev.event_ticketing.entity.User;
import com.omardev.event_ticketing.enums.EventStatus;
import com.omardev.event_ticketing.exception.EventNotFoundException;
import com.omardev.event_ticketing.exception.UnauthorizedException;
import com.omardev.event_ticketing.exception.UserNotFoundException;
import com.omardev.event_ticketing.mapper.EventMapper;
import com.omardev.event_ticketing.repository.EventRepository;
import com.omardev.event_ticketing.repository.UserRepository;
import com.omardev.event_ticketing.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    /**
     * Get the current authenticated user's Keycloak ID.
     */
    private String getCurrentUserKeycloakId() {
        Jwt jwt = (Jwt) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return jwt.getSubject();
    }

    /**
     * Get the current authenticated user from DB.
     */
    private User getCurrentUser() {
        String keycloakId = getCurrentUserKeycloakId();
        return userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException(keycloakId));
    }


    /**
     * Fetch all events with pagination.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> getAllEvents(Pageable pageable) {

        // 1. Fetch events from database
        Page<Event> events = eventRepository.findAll(pageable);

        // 2. Map entities → DTOs
        return events.map(eventMapper::toResponse);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> getPublishedEvents(
            Pageable pageable,
            String keyword
    ) {

        Page<Event> events;

        // 1. If a keyword is provided → search
        if (keyword != null && !keyword.isBlank()) {

            events = eventRepository.searchPublishedEvents(keyword, pageable);

        } else {
            // 2. Otherwise → get all published events
            events = eventRepository.findByStatus(
                    EventStatus.PUBLISHED,
                    pageable
            );
        }

        // 3. Map Entity → DTO
        return events.map(eventMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(UUID eventId) {

        // 1. Fetch event from DB
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        // 2. Map Entity → DTO
        return eventMapper.toResponse(event);
    }

    @Override
    @Transactional
    public EventResponse createEvent(CreateEventRequest request) {

        // 1. Get current user
        User organizer = getCurrentUser();

        // 2. Map request → entity
        Event event = eventMapper.toEntity(request);

        // 3. Set business fields
        event.setOrganizer(organizer);
        event.setStatus(EventStatus.DRAFT);
        event.setAvailableTickets(event.getCapacity());

        // 4. Link TicketTypes
        if (event.getTicketTypes() != null) {
            event.getTicketTypes().forEach(tt -> tt.setEvent(event));
        }

        // 5. Save + return DTO
        return eventMapper.toResponse(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventResponse updateEvent(UUID eventId, UpdateEventRequest request) {

        // 1. Get current user
        User currentUser = getCurrentUser();

        // 2. Fetch event
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        // 3.  Ownership check (VERY IMPORTANT)
        if (!event.getOrganizer().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not allowed to update this event");
        }

        // 4. Update fields (only if not null)
        if (request.title() != null) {
            event.setTitle(request.title());
        }

        if (request.description() != null) {
            event.setDescription(request.description());
        }

        if (request.location() != null) {
            event.setLocation(request.location());
        }

        if (request.bannerUrl() != null) {
            event.setBannerUrl(request.bannerUrl());
        }

        if (request.startDate() != null) {
            event.setStartDate(request.startDate());
        }

        if (request.endDate() != null) {
            event.setEndDate(request.endDate());
        }

        if (request.capacity() != null) {
            event.setCapacity(request.capacity());
        }

        // 5. Save updated event
        Event updatedEvent = eventRepository.save(event);

        // 6. Return DTO
        return eventMapper.toResponse(updatedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> getMyEvents(Pageable pageable, EventStatus status) {

        User user = getCurrentUser();

        Page<Event> events = (status != null)
                ? eventRepository.findByOrganizer_IdAndStatus(user.getId(), status, pageable)
                : eventRepository.findByOrganizer_Id(user.getId(), pageable);

        return events.map(eventMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteEvent(UUID eventId) {

        // 1. Get a current authenticated user
        User currentUser = getCurrentUser();

        // 2. Fetch event from DB
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        // 3. Ownership check (VERY IMPORTANT)
        if (!event.getOrganizer().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not allowed to delete this event");
        }

        // 4. Delete event
        eventRepository.delete(event);
    }
}