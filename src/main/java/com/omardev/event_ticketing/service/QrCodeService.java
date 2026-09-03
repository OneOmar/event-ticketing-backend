package com.omardev.event_ticketing.service;

import com.omardev.event_ticketing.entity.QrCode;
import com.omardev.event_ticketing.entity.Ticket;

public interface QrCodeService {

    /**
     * Generate and return a new QR code entity for a ticket.
     */
    QrCode generateForTicket(Ticket ticket);

    /**
     * Generate QR code image (PNG) from a code.
     */
    byte[] generateImage(String code);
}