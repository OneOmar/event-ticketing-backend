package com.omardev.event_ticketing.enums;

public enum QrCodeStatus {
    ACTIVE,   // usable (valid QR)
    USED,     // already scanned
    EXPIRED   // no longer valid (time-based)
}