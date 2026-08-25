package com.omardev.event_ticketing.service.impl;

import com.omardev.event_ticketing.dto.request.ValidateTicketRequest;
import com.omardev.event_ticketing.dto.response.ValidateTicketResponse;
import com.omardev.event_ticketing.entity.QrCode;
import com.omardev.event_ticketing.enums.QrCodeStatus;
import com.omardev.event_ticketing.exception.ApiException;
import com.omardev.event_ticketing.repository.QrCodeRepository;
import com.omardev.event_ticketing.service.TicketValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TicketValidationServiceImpl implements TicketValidationService {

    private final QrCodeRepository qrCodeRepository;

    @Override
    @Transactional
    public ValidateTicketResponse validate(ValidateTicketRequest request) {

        // 1. Find QR code by value
        QrCode qrCode = qrCodeRepository
                .findByCode(request.qrCode())
                .orElseThrow(() -> new ApiException("QR code not found"));

        // 2. Check QR status
        if (qrCode.getStatus() != QrCodeStatus.ACTIVE) {
            return new ValidateTicketResponse(
                    false,
                    "QR code is not valid",
                    null,
                    null
            );
        }

        // 3. Check expiration
        if (qrCode.getExpiresAt() != null &&
                qrCode.getExpiresAt().isBefore(LocalDateTime.now())) {

            return new ValidateTicketResponse(
                    false,
                    "QR code is expired",
                    null,
                    null
            );
        }

        // 4. Check if already used
        if (qrCode.getUsedAt() != null) {
            return new ValidateTicketResponse(
                    false,
                    "Ticket already used",
                    null,
                    null
            );
        }

        // Next step will handle success case

        return null; // temporary
    }
}