package com.omardev.event_ticketing.service.impl;

import com.omardev.event_ticketing.dto.response.TicketResponse;
import com.omardev.event_ticketing.entity.Event;
import com.omardev.event_ticketing.entity.QrCode;
import com.omardev.event_ticketing.entity.Ticket;
import com.omardev.event_ticketing.entity.User;
import com.omardev.event_ticketing.enums.TicketStatus;
import com.omardev.event_ticketing.exception.EventNotFoundException;
import com.omardev.event_ticketing.exception.UserNotFoundException;
import com.omardev.event_ticketing.repository.EventRepository;
import com.omardev.event_ticketing.repository.TicketRepository;
import com.omardev.event_ticketing.repository.QrCodeRepository;
import com.omardev.event_ticketing.service.QrCodeService;
import com.omardev.event_ticketing.service.TicketService;
import com.omardev.event_ticketing.repository.UserRepository;
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
    private final QrCodeRepository qrCodeRepository;
    private final QrCodeService qrCodeService;
    private final UserRepository userRepository;

    /**
     * Purchase a ticket for an event.
     */
    @Override
    @Transactional
    public TicketResponse purchaseTicket(UUID eventId) {

        // 1. Get current user (buyer)
        User user = getCurrentUser();

        // 2. Get event
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        // 3. Create ticket
        Ticket ticket = Ticket.builder()
                .event(event)
                .owner(user)

                .ticketCode("TCK-" + UUID.randomUUID().toString().substring(0, 8))
                .status(TicketStatus.ACTIVE)

                // TEMP (we fix later)
                .ticketType(event.getTicketTypes().getFirst())

                .createdAt(LocalDateTime.now())
                .build();

        // 4. Save ticket first (needed for relation)
        Ticket savedTicket = ticketRepository.save(ticket);

        // 5. Generate QR code for this ticket
        QrCode qrCode = qrCodeService.generateForTicket(savedTicket);

        // 6. Save QR code
        qrCodeRepository.save(qrCode);

        // 7. Build response
        return new TicketResponse(
                savedTicket.getId(),
                event.getId(),
                qrCode.getCode(), // return token for now
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