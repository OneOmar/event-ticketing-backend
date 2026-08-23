package com.omardev.event_ticketing.repository;


import com.omardev.event_ticketing.entity.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface QrCodeRepository extends JpaRepository<QrCode, UUID> {

    Optional<QrCode> findByCode(String code);

}
