package com.omardev.event_ticketing.service;

import com.omardev.event_ticketing.dto.request.ValidateTicketRequest;
import com.omardev.event_ticketing.dto.response.ValidateTicketResponse;

public interface TicketValidationService {

    /**
     * Validates a ticket based on its QR code.
     *
     * @param request contains QR code to validate
     * @return validation result (valid / invalid + message)
     */
    ValidateTicketResponse validateTicket(ValidateTicketRequest request);
}