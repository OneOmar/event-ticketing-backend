package com.omardev.event_ticketing.service;

import com.omardev.event_ticketing.dto.request.ValidateTicketRequest;
import com.omardev.event_ticketing.dto.response.ValidateTicketResponse;

public interface TicketValidationService {

    /**
     * Validate a ticket using QR code
     */
    ValidateTicketResponse validate(ValidateTicketRequest request);
}
