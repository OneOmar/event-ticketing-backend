package com.omardev.event_ticketing.service.impl;

import com.omardev.event_ticketing.dto.request.PurchaseTicketRequest;
import com.omardev.event_ticketing.dto.response.TicketResponse;
import com.omardev.event_ticketing.entity.*;
import com.omardev.event_ticketing.enums.EventStatus;
import com.omardev.event_ticketing.enums.TicketStatus;
import com.omardev.event_ticketing.exception.*;
import com.omardev.event_ticketing.repository.*;
import com.omardev.event_ticketing.service.QrCodeService;
import com.omardev.event_ticketing.service.TicketService;
import com.omardev.event_ticketing.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final QrCodeRepository qrCodeRepository;
    private final QrCodeService qrCodeService;
    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional
    public List<TicketResponse> purchaseTicket(PurchaseTicketRequest request) {

        // 1. Get the authenticated user
        User user = currentUserProvider.getCurrentUser();

        // 2. Fetch event
        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new EventNotFoundException(request.eventId()));

        // 3. Fetch ticket type with lock (prevents concurrent overselling)
        TicketType ticketType = ticketTypeRepository
                .findByIdForUpdate(request.ticketTypeId())
                .orElseThrow(() -> new TicketTypeNotFoundException(request.ticketTypeId()));

        // 4. Validate event status
        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new ApiException("Event is not available for ticket purchase");
        }

        // 5. Ensure the ticket type belongs to an event
        if (!ticketType.getEvent().getId().equals(event.getId())) {
            throw new ApiException("Ticket type does not belong to this event");
        }

        // 6. Ensure the ticket type is active
        if (!ticketType.isActive()) {
            throw new ApiException("Ticket type is not active");
        }

        // 7. Check availability
        if (ticketType.getAvailableQuantity() < request.quantity()) {
            throw new TicketTypeSoldOutException(ticketType.getId());
        }

        // 8. Decrease stock
        ticketType.setAvailableQuantity(
                ticketType.getAvailableQuantity() - request.quantity()
        );

        // 9. Prepare a response list
        List<TicketResponse> responses = new ArrayList<>();

        // Use single timestamp for consistency
        LocalDateTime now = LocalDateTime.now();

        // 10. Create tickets
        for (int i = 0; i < request.quantity(); i++) {

            Ticket ticket = Ticket.builder()
                    .event(event)
                    .owner(user)
                    .ticketType(ticketType)
                    .ticketCode("TCK-" + UUID.randomUUID().toString().substring(0, 8))
                    .status(TicketStatus.ACTIVE)
                    .createdAt(now)
                    .build();

            Ticket savedTicket = ticketRepository.save(ticket);

            // Generate QR code
            QrCode qrCode = qrCodeService.generateForTicket(savedTicket);
            qrCodeRepository.save(qrCode);

            // Build response
            responses.add(new TicketResponse(
                    savedTicket.getId(),
                    event.getId(),
                    qrCode.getCode(),
                    savedTicket.getCreatedAt()
            ));
        }

        // 11. Return all tickets
        return responses;
    }
}