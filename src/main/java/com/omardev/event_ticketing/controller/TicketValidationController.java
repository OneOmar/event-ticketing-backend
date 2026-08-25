package com.omardev.event_ticketing.controller;

import com.omardev.event_ticketing.dto.request.ValidateTicketRequest;
import com.omardev.event_ticketing.dto.response.ValidateTicketResponse;
import com.omardev.event_ticketing.service.TicketValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("/validate")
    public ResponseEntity<ValidateTicketResponse> validateTicket(
            @RequestBody ValidateTicketRequest request
    ) {

        // Call service layer (business logic)
        ValidateTicketResponse response =
                ticketValidationService.validate(request);

        // Always 200 → result is inside response (valid / invalid)
        return ResponseEntity.ok(response);
    }
}