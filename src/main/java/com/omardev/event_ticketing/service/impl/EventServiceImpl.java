package com.omardev.event_ticketing.service.impl;

import com.omardev.event_ticketing.dto.request.CreateEventRequest;
import com.omardev.event_ticketing.dto.response.EventResponse;
import com.omardev.event_ticketing.entity.Event;
import com.omardev.event_ticketing.entity.TicketType;
import com.omardev.event_ticketing.entity.User;
import com.omardev.event_ticketing.enums.EventStatus;
import com.omardev.event_ticketing.exception.UserNotFoundException;
import com.omardev.event_ticketing.mapper.EventMapper;
import com.omardev.event_ticketing.mapper.TicketTypeMapper;
import com.omardev.event_ticketing.repository.EventRepository;
import com.omardev.event_ticketing.repository.UserRepository;
import com.omardev.event_ticketing.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final TicketTypeMapper ticketTypeMapper;

    @Override
    public EventResponse createEvent(CreateEventRequest request) {

        // 1. Get authenticated user
        Jwt jwt = (Jwt) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String keycloakId = jwt.getSubject();

        User organizer = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException(keycloakId));

        // 2. Map request → Event
        Event event = eventMapper.toEntity(request);

        // 3. Set business fields
        event.setOrganizer(organizer);
        event.setStatus(EventStatus.DRAFT);
        event.setAvailableTickets(event.getCapacity());

        // 4. Handle TicketTypes (relation only)
        if (event.getTicketTypes() != null) {
            event.getTicketTypes()
                    .forEach(tt -> tt.setEvent(event));
        }

        // 5. Save
        Event savedEvent = eventRepository.save(event);

        // 6. Map to response
        return eventMapper.toResponse(savedEvent);
    }
}