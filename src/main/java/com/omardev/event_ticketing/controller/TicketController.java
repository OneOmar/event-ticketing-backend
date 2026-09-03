package com.omardev.event_ticketing.controller;

import com.omardev.event_ticketing.dto.request.PurchaseTicketRequest;
import com.omardev.event_ticketing.dto.response.TicketResponse;
import com.omardev.event_ticketing.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Ticket management endpoints.
 */
@Tag(name = "Tickets", description = "Ticket purchase and user tickets")
@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    /**
     * Purchase tickets for an event.
     */
    @Operation(
            summary = "Purchase tickets",
            description = "Requires role: ATTENDEE"
    )
    @PreAuthorize("hasRole('ATTENDEE')")
    @PostMapping("/purchase")
    public ResponseEntity<List<TicketResponse>> purchaseTicket(
            @Valid @RequestBody PurchaseTicketRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ticketService.purchaseTicket(request));
    }

    /**
     * Get current user's tickets.
     */
    @Operation(
            summary = "Get my tickets",
            description = "Requires role: ATTENDEE"
    )
    @PreAuthorize("hasRole('ATTENDEE')")
    @GetMapping("/me")
    public ResponseEntity<List<TicketResponse>> getMyTickets() {

        return ResponseEntity.ok(
                ticketService.getMyTickets()
        );
    }

    /**
     * Download the QR code image for a given ticket.
     * Accessible only by the ticket owner (ATTENDEE role).
     * Returns the QR code as a PNG file for download.
     */
    @GetMapping("/{id}/qr")
    @PreAuthorize("hasRole('ATTENDEE')")
    public ResponseEntity<byte[]> downloadQr(@PathVariable UUID id) {

        byte[] qrImage = ticketService.getTicketQr(id);

        return ResponseEntity.ok()
                // Dynamic filename (better UX)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=ticket-" + id + ".png")
                .contentType(MediaType.IMAGE_PNG)
                .body(qrImage);
    }
}