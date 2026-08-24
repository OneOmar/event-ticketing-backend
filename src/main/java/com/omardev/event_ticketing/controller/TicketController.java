package com.omardev.event_ticketing.controller;

import com.omardev.event_ticketing.dto.request.PurchaseTicketRequest;
import com.omardev.event_ticketing.dto.response.TicketResponse;
import com.omardev.event_ticketing.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    /**
     * Purchase a ticket (by event + ticket type).
     */
    @PostMapping("/purchase")
    public ResponseEntity<TicketResponse> purchaseTicket(
            @Valid @RequestBody PurchaseTicketRequest request
    ) {

        // Delegate to service
        TicketResponse response = ticketService.purchaseTicket(request);

        // Return 201 CREATED
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}