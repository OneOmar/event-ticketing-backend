package com.omardev.event_ticketing.controller;

import com.omardev.event_ticketing.dto.request.ValidateTicketRequest;
import com.omardev.event_ticketing.dto.response.ValidateTicketResponse;
import com.omardev.event_ticketing.service.TicketValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketValidationController {

    private final TicketValidationService ticketValidationService;

    /**
     * Validate a ticket using QR code.
     * Called by scanner (mobile or web).
     */
    @PreAuthorize("hasRole('STAFF')")
    @PostMapping("/validate")
    public ResponseEntity<ValidateTicketResponse> validateTicket(
            @Valid @RequestBody ValidateTicketRequest request
    ) {

        // Delegate validation logic to service
        ValidateTicketResponse response =
                ticketValidationService.validateTicket(request);

        // Return result (valid / invalid inside response)
        return ResponseEntity.ok(response);
    }
}