package com.omardev.event_ticketing.enums;

public enum TicketStatus {
    ACTIVE,     // Ticket is valid and usable
    USED,       // Already scanned
    CANCELLED,  // Cancelled by user/admin
    EXPIRED     // Event passed or QR expired
}