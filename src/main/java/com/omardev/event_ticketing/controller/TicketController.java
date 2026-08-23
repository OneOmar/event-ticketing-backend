package com.omardev.event_ticketing.controller;

import com.omardev.event_ticketing.dto.response.TicketResponse;
import com.omardev.event_ticketing.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    /**
     * Purchase a ticket for an event.
     */
    @PostMapping("/purchase/{eventId}")
    public ResponseEntity<TicketResponse> purchaseTicket(
            @PathVariable UUID eventId
    ) {

        // Call service layer
        TicketResponse response = ticketService.purchaseTicket(eventId);

        // Return 201 CREATED
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}