package com.omardev.event_ticketing.mapper;

import com.omardev.event_ticketing.dto.request.CreateTicketTypeRequest;
import com.omardev.event_ticketing.dto.response.TicketTypeResponse;
import com.omardev.event_ticketing.entity.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE // ignore fields not mapped
)
public interface TicketTypeMapper {

    /**
     * Map CreateTicketTypeRequest → TicketType entity.
     * Note: 'event' is ignored and will be set manually in the service layer.
     */
    @Mapping(target = "event", ignore = true)
    TicketType toEntity(CreateTicketTypeRequest request);

    /**
     * Map TicketType entity → TicketTypeResponse DTO.
     */
    TicketTypeResponse toResponse(TicketType ticketType);
}