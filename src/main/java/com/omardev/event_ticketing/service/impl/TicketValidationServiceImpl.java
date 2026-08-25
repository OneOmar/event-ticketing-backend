package com.omardev.event_ticketing.service.impl;

import com.omardev.event_ticketing.dto.request.ValidateTicketRequest;
import com.omardev.event_ticketing.dto.response.ValidateTicketResponse;
import com.omardev.event_ticketing.entity.QrCode;
import com.omardev.event_ticketing.entity.TicketValidation;
import com.omardev.event_ticketing.enums.QrCodeStatus;
import com.omardev.event_ticketing.enums.TicketValidationMethod;
import com.omardev.event_ticketing.enums.TicketValidationStatus;
import com.omardev.event_ticketing.exception.ApiException;
import com.omardev.event_ticketing.repository.QrCodeRepository;
import com.omardev.event_ticketing.repository.TicketValidationRepository;
import com.omardev.event_ticketing.service.TicketValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TicketValidationServiceImpl implements TicketValidationService {

    private final TicketValidationRepository ticketValidationRepository;
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

            saveValidation(qrCode, TicketValidationStatus.FAILED, "QR not active");

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

            saveValidation(qrCode, TicketValidationStatus.FAILED, "QR expired");

            return new ValidateTicketResponse(
                    false,
                    "QR code is expired",
                    null,
                    null
            );
        }

        // 4. Check if already used
        if (qrCode.getUsedAt() != null) {

            saveValidation(qrCode, TicketValidationStatus.FAILED, "Ticket already used");

            return new ValidateTicketResponse(
                    false,
                    "Ticket already used",
                    null,
                    null
            );
        }

        // 5. Mark QR as used (state change)
        qrCode.setUsedAt(LocalDateTime.now());

        // Optional: update status
        qrCode.setStatus(QrCodeStatus.USED);

        // Save update
        qrCodeRepository.save(qrCode);

        // Save validation success
        saveValidation(qrCode, TicketValidationStatus.SUCCESS, "Ticket validated");

        // 6. Build SUCCESS response
        return new ValidateTicketResponse(
                true,
                "Ticket is valid",
                qrCode.getTicket().getTicketCode(),
                qrCode.getTicket().getEvent().getTitle()
        );
    }

    /**
     * Save validation history (SUCCESS or FAILED)
     */
    private void saveValidation(
            QrCode qrCode,
            TicketValidationStatus status,
            String notes
    ) {

        TicketValidation validation = TicketValidation.builder()
                .ticket(qrCode.getTicket())
                .status(status)
                .method(TicketValidationMethod.QR_SCAN)
                .notes(notes)
                // validatedAt handled by @PrePersist
                .build();

        ticketValidationRepository.save(validation);
    }
}