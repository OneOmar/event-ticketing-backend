package com.omardev.event_ticketing.service.impl;

import com.omardev.event_ticketing.dto.request.ValidateTicketRequest;
import com.omardev.event_ticketing.dto.response.ValidateTicketResponse;
import com.omardev.event_ticketing.entity.QrCode;
import com.omardev.event_ticketing.exception.ApiException;
import com.omardev.event_ticketing.repository.QrCodeRepository;
import com.omardev.event_ticketing.service.TicketValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return null; // temporary
    }
}