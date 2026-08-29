package com.omardev.event_ticketing.repository;

import com.omardev.event_ticketing.entity.QrCode;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface QrCodeRepository extends JpaRepository<QrCode, UUID> {

    Optional<QrCode> findByCode(String code);

    /**
     * Fetch QR code with DB lock to prevent concurrent validation
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT q FROM QrCode q WHERE q.code = :code")
    Optional<QrCode> findByCodeForUpdate(String code);
}