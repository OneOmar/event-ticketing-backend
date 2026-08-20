package com.omardev.event_ticketing.mapper;

import com.omardev.event_ticketing.dto.request.CreateEventRequest;
import com.omardev.event_ticketing.dto.response.EventResponse;
import com.omardev.event_ticketing.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = TicketTypeMapper.class, // handle ticketTypes mapping
        unmappedTargetPolicy = ReportingPolicy.IGNORE // ignore unused fields
)
public interface EventMapper {

    /**
     * Map CreateEventRequest → Event entity.
     * Note: organizer and relations are set manually in service.
     */
    Event toEntity(CreateEventRequest request);

    /**
     * Map Event entity → EventResponse DTO.
     * - Flatten organizer (id + name)
     * - ticketTypes handled automatically via TicketTypeMapper
     */
    @Mapping(source = "organizer.id", target = "organizerId")
    @Mapping(target = "organizerName", expression = "java(getOrganizerName(event))")
    EventResponse toResponse(Event event);

    /**
     * Helper to build organizer full name.
     */
    default String getOrganizerName(Event event) {
        if (event.getOrganizer() == null) return null;
        return event.getOrganizer().getFirstName() + " " +
                event.getOrganizer().getLastName();
    }
}