package com.omardev.event_ticketing.service.impl;

import com.omardev.event_ticketing.dto.request.CreateEventRequest;
import com.omardev.event_ticketing.dto.response.EventResponse;
import com.omardev.event_ticketing.entity.Event;
import com.omardev.event_ticketing.entity.TicketType;
import com.omardev.event_ticketing.entity.User;
import com.omardev.event_ticketing.enums.EventStatus;
import com.omardev.event_ticketing.repository.EventRepository;
import com.omardev.event_ticketing.repository.UserRepository;
import com.omardev.event_ticketing.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public EventResponse createEvent(CreateEventRequest request) {

        // Get the authenticated user (organizer)
        Jwt jwt = (Jwt) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String keycloakId = jwt.getSubject();

        // Fetch the organizer user
        User organizer = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create the event
        Event event = Event.builder()
                .title(request.title())
                .description(request.description())
                .location(request.location())
                .bannerUrl(request.bannerUrl())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .capacity(request.capacity())
                .availableTickets(request.capacity())
                .status(EventStatus.DRAFT)
                .organizer(organizer)
                .build();

        // Add TicketTypes
        if (request.ticketTypes() != null) {

            List<TicketType> ticketTypes = request.ticketTypes()
                    .stream()
                    .map(dto -> {
                        TicketType tt = new TicketType();
                        tt.setName(dto.name());
                        tt.setDescription(dto.description());
                        tt.setPrice(dto.price());
                        tt.setQuantity(dto.quantity());
                        tt.setEvent(event); // VERY IMPORTANT
                        return tt;
                    })
                    .toList();

            event.setTicketTypes(ticketTypes);
        }

        // Save the event
        Event savedEvent = eventRepository.save(event);

        // Convert to Response
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getBannerUrl(),
                event.getStartDate(),
                event.getEndDate(),
                event.getCapacity(),
                event.getAvailableTickets(),
                event.getStatus(),
                event.getOrganizer().getId(),
                event.getOrganizer().getFirstName() + " " + event.getOrganizer().getLastName(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}
