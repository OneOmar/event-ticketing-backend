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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
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

        // 1. Current user
        User user = currentUserProvider.getCurrentUser();
        log.info("User {} purchasing {} ticket(s) for event {}",
                user.getId(), request.quantity(), request.eventId());

        // 2. Fetch event
        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> {
                    log.warn("Event not found: {}", request.eventId());
                    return new EventNotFoundException(request.eventId());
                });

        // 3. Fetch ticket type (locked)
        TicketType ticketType = ticketTypeRepository
                .findByIdForUpdate(request.ticketTypeId())
                .orElseThrow(() -> {
                    log.warn("Ticket type not found: {}", request.ticketTypeId());
                    return new TicketTypeNotFoundException(request.ticketTypeId());
                });

        // 4. Validate event
        if (event.getStatus() != EventStatus.PUBLISHED) {
            log.warn("Event {} not published", event.getId());
            throw new ApiException("Event is not available for ticket purchase");
        }

        // 5. Validate relation
        if (!ticketType.getEvent().getId().equals(event.getId())) {
            log.warn("TicketType {} not linked to event {}", ticketType.getId(), event.getId());
            throw new ApiException("Ticket type does not belong to this event");
        }

        // 6. Validate active
        if (!ticketType.isActive()) {
            log.warn("Inactive ticket type {}", ticketType.getId());
            throw new ApiException("Ticket type is not active");
        }

        // 7. Check stock
        if (ticketType.getAvailableQuantity() < request.quantity()) {
            log.warn("Insufficient stock for type {} (req={}, avail={})",
                    ticketType.getId(), request.quantity(), ticketType.getAvailableQuantity());
            throw new TicketTypeSoldOutException(ticketType.getId());
        }

        // 8. Decrease stock
        ticketType.setAvailableQuantity(
                ticketType.getAvailableQuantity() - request.quantity()
        );

        List<TicketResponse> responses = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // 9. Create tickets
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

            // Generate QR
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

        // 10. Success
        log.info("User {} purchased {} ticket(s) for event {}",
                user.getId(), responses.size(), event.getId());

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketResponse> getMyTickets() {

        // 1. Current user
        User user = currentUserProvider.getCurrentUser();
        log.info("Fetching tickets for user {}", user.getId());

        // 2. Fetch tickets
        List<Ticket> tickets = ticketRepository.findByOwnerIdWithQrCodes(user.getId());

        // 3. Map → response
        return tickets.stream()
                .map(ticket -> {

                    String qrCode = ticket.getQrCodes().isEmpty()
                            ? null
                            : ticket.getQrCodes().getFirst().getCode();

                    return new TicketResponse(
                            ticket.getId(),
                            ticket.getEvent().getId(),
                            qrCode,
                            ticket.getCreatedAt()
                    );
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getTicketQr(UUID ticketId) {

        // 1. Current user
        User user = currentUserProvider.getCurrentUser();
        log.info("User {} requesting QR for ticket {}", user.getId(), ticketId);

        // 2. Fetch ticket
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> {
                    log.warn("Ticket not found: {}", ticketId);
                    return new TicketNotFoundException(ticketId);
                });

        // 3. Ownership check
        if (!ticket.getOwner().getId().equals(user.getId())) {
            log.warn("Unauthorized QR access attempt by user {} for ticket {}",
                    user.getId(), ticketId);
            throw new ApiException("You are not allowed to access this ticket");
        }

        // 4. Get latest QR
        QrCode qrCode = ticket.getQrCodes().isEmpty()
                ? null
                : ticket.getQrCodes().getFirst();

        if (qrCode == null) {
            log.warn("QR not found for ticket {}", ticketId);
            throw new ApiException("QR code not found for this ticket");
        }

        // 5. Generate image via service
        log.info("QR image generated for ticket {}", ticketId);
        return qrCodeService.generateImage(qrCode.getCode());
    }
}