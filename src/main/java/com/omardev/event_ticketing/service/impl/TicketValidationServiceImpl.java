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
import com.omardev.event_ticketing.util.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TicketValidationServiceImpl implements TicketValidationService {

    private final CurrentUserProvider currentUserProvider;
    private final TicketValidationRepository ticketValidationRepository;
    private final QrCodeRepository qrCodeRepository;

    @Override
    @Transactional
    public ValidateTicketResponse validateTicket(ValidateTicketRequest request) {

        // 1. Fetch QR with lock (avoid double scan)
        QrCode qrCode = qrCodeRepository
                .findByCodeForUpdate(request.qrCode())
                .orElseThrow(() -> new ApiException("QR code not found"));


        // 2. Check already used
        if (qrCode.getUsedAt() != null) {
            saveValidation(qrCode, TicketValidationStatus.FAILED, "Already used");
            return new ValidateTicketResponse(
                    false,
                    "Ticket already used",
                    null,
                    null
            );
        }

        // 3. Check status
        if (qrCode.getStatus() != QrCodeStatus.ACTIVE) {
            saveValidation(qrCode, TicketValidationStatus.FAILED, "QR not active");
            return new ValidateTicketResponse(
                    false,
                    "QR code is not valid",
                    null,
                    null
            );
        }

        // 4. Check expiration
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

        // 5. Mark as used
        qrCode.setUsedAt(LocalDateTime.now());
        qrCode.setStatus(QrCodeStatus.USED);

        // 6. Save validation
        saveValidation(qrCode, TicketValidationStatus.SUCCESS, "Validated");

        return new ValidateTicketResponse(
                true,
                "Access granted",
                qrCode.getTicket().getTicketCode(),
                qrCode.getTicket().getEvent().getTitle()
        );
    }

    /**
     * Save ticket validation history (SUCCESS or FAILED)
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
                .validatedBy(currentUserProvider.getCurrentUser())
                .notes(notes)
                .validatedAt(LocalDateTime.now())
                .build();

        ticketValidationRepository.save(validation);
    }
}