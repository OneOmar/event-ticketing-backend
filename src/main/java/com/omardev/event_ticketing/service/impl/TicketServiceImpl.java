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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final QrCodeRepository qrCodeRepository;
    private final QrCodeService qrCodeService;
    private final UserRepository userRepository;

    /**
     * Purchase a ticket for an event.
     */
//    @Override
//    @Transactional
//    public TicketResponse purchaseTicket(UUID eventId) {
//
//        // 1. Get current user (buyer)
//        User user = getCurrentUser();
//
//        // 2. Get event
//        Event event = eventRepository.findById(eventId)
//                .orElseThrow(() -> new EventNotFoundException(eventId));
//
//        // 3. VALIDATION (VERY IMPORTANT)
//
//        // Event not published
//        if (event.getStatus() != EventStatus.PUBLISHED) {
//            throw new EventNotAvailableException(eventId);
//
//        }
//
//        // No tickets left
//        if (event.getAvailableTickets() <= 0) {
//            throw new NoTicketsAvailableException(eventId);
//        }
//
//        // Ensure ticket type exists (TEMP logic)
//        if (event.getTicketTypes().isEmpty()) {
//            throw new RuntimeException("No ticket types defined for this event");
//        }
//
//        // 4. Decrease available tickets
//        event.setAvailableTickets(event.getAvailableTickets() - 1);
//
//        // 5. Create ticket
//        Ticket ticket = Ticket.builder()
//                .event(event)
//                .owner(user)
//                .ticketCode("TCK-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10))
//                .status(TicketStatus.ACTIVE)
//                .ticketType(event.getTicketTypes().get(0)) // temp
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        // 6. Save ticket first (needed for relation)
//        Ticket savedTicket = ticketRepository.save(ticket);
//
//        // 7. Generate QR code for this ticket
//        QrCode qrCode = qrCodeService.generateForTicket(savedTicket);
//
//        // 8. Save QR code
//        qrCodeRepository.save(qrCode);
//
//        // 9. Build response
//        return new TicketResponse(
//                savedTicket.getId(),
//                event.getId(),
//                qrCode.getCode(), // return token for now
//                savedTicket.getCreatedAt()
//        );
//    }

    @Override
    @Transactional
    public TicketResponse purchaseTicket(PurchaseTicketRequest request) {

        // 1. Get current authenticated user
        User user = getCurrentUser();

        // 2. Fetch event
        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new EventNotFoundException(request.eventId()));

        // 3. Fetch ticket type
        TicketType ticketType = ticketTypeRepository.findById(request.ticketTypeId())
                .orElseThrow(() -> new ApiException("Ticket type not found"));

        // 4. Validate event is purchasable
        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new ApiException("Event is not available for ticket purchase");
        }

        // 5. Ensure ticket type belongs to this event (security + consistency)
        if (!ticketType.getEvent().getId().equals(event.getId())) {
            throw new ApiException("Ticket type does not belong to this event");
        }

        // 6. Ensure ticket type is active
        if (!ticketType.isActive()) {
            throw new ApiException("Ticket type is not active");
        }

        // 7. Check availability for this specific ticket type
        if (ticketType.getAvailableQuantity() <= 0) {
            throw new ApiException("No tickets available for this ticket type");
        }

        // 8. Decrease available stock (managed by JPA transaction)
        ticketType.setAvailableQuantity(ticketType.getAvailableQuantity() - 1);

        // 9. Create ticket
        Ticket ticket = Ticket.builder()
                .event(event)
                .owner(user)
                .ticketType(ticketType)
                .ticketCode("TCK-" + UUID.randomUUID().toString().substring(0, 8))
                .status(TicketStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        // 10. Save ticket
        Ticket savedTicket = ticketRepository.save(ticket);

        // 11. Generate QR code for this ticket
        QrCode qrCode = qrCodeService.generateForTicket(savedTicket);

        // 12. Persist QR code
        qrCodeRepository.save(qrCode);

        // 13. Return response DTO
        return new TicketResponse(
                savedTicket.getId(),
                event.getId(),
                qrCode.getCode(),
                savedTicket.getCreatedAt()
        );
    }

    /**
     * Get the current authenticated user's Keycloak ID.
     */
    private String getCurrentUserKeycloakId() {

        var authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        // Ensure authentication exists and is valid
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new RuntimeException("Invalid authentication context");
        }

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
}