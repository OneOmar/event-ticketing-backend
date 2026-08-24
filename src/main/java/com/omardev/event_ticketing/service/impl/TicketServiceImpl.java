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
                .orElseThrow(() -> new TicketTypeNotFoundException(request.ticketTypeId()));

        // 4. Validate event is purchasable
        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new ApiException("Event is not available for ticket purchase");
        }

        // 5. Ensure the ticket type belongs to this event (security + consistency)
        if (!ticketType.getEvent().getId().equals(event.getId())) {
            throw new ApiException("Ticket type does not belong to this event");
        }

        // 6. Ensure the ticket type is active
        if (!ticketType.isActive()) {
            throw new ApiException("Ticket type is not active");
        }

        // 7. Check availability for this specific ticket type
        if (ticketType.getAvailableQuantity() <= 0) {
            throw new TicketTypeSoldOutException(ticketType.getId());
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