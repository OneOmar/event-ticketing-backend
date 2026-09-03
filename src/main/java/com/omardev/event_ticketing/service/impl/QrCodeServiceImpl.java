package com.omardev.event_ticketing.service.impl;

import com.omardev.event_ticketing.entity.QrCode;
import com.omardev.event_ticketing.entity.Ticket;
import com.omardev.event_ticketing.enums.QrCodeStatus;
import com.omardev.event_ticketing.service.QrCodeService;
import com.omardev.event_ticketing.util.QrCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QrCodeServiceImpl implements QrCodeService {

    private final QrCodeGenerator qrCodeGenerator;

    @Override
    public QrCode generateForTicket(Ticket ticket) {

        // 1. Generate a unique QR token (used later for validation)
        String code = UUID.randomUUID().toString();

        // 2. Generate QR image (optional for now, but ready for future use)
        byte[] qrImage = qrCodeGenerator.generate(code);
        // You can store it later (DB, S3...) — for now we just generate it

        // 3. Build QrCode entity
        QrCode qrCode = QrCode.builder()
                .id(UUID.randomUUID()) // manual UUID
                .code(code)
                .status(QrCodeStatus.ACTIVE)
                .expiresAt(LocalDateTime.now().plusHours(6)) // simple expiration rule
                .ticket(ticket)
                .build();

        // 4. Return entity (saving will be done in another layer)
        return qrCode;
    }

    /**
     * Generate QR code image (PNG) from a given code.
     */
    @Override
    public byte[] generateImage(String code) {
        return qrCodeGenerator.generate(code);
    }
}