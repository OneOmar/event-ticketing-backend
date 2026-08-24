package com.omardev.event_ticketing.repository;


import com.omardev.event_ticketing.entity.TicketType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TicketTypeRepository extends JpaRepository<TicketType, UUID> {

    // Lock the row in DB to prevent concurrent updates (avoid overselling)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    // Custom query to fetch TicketType by id with lock
    @Query("SELECT tt FROM TicketType tt WHERE tt.id = :id")
    // Used in purchase flow to safely read + update availableQuantity
    Optional<TicketType> findByIdForUpdate(@Param("id") UUID id);
}