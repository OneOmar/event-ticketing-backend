package com.omardev.event_ticketing.controller;

import com.omardev.event_ticketing.dto.request.ValidateTicketRequest;
import com.omardev.event_ticketing.dto.response.ValidateTicketResponse;
import com.omardev.event_ticketing.service.TicketValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Ticket validation endpoints (scanner side).
 */
@Tag(name = "Ticket Validation", description = "Validate tickets using QR codes")
@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketValidationController {

    private final TicketValidationService ticketValidationService;

    /**
     * Validate a ticket using QR code.
     */
    @Operation(
            summary = "Validate ticket",
            description = """
                    Validate a ticket using QR code.
                    Required role: STAFF
                    Used by scanner (mobile/web)
                    """
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('STAFF')")
    @PostMapping("/validate")
    public ResponseEntity<ValidateTicketResponse> validateTicket(
            @Valid @RequestBody ValidateTicketRequest request
    ) {

        return ResponseEntity.ok(
                ticketValidationService.validateTicket(request)
        );
    }
}