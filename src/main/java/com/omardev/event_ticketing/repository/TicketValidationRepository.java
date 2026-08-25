package com.omardev.event_ticketing.repository;

import com.omardev.event_ticketing.entity.TicketValidation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for ticket validation history
 */
public interface TicketValidationRepository extends JpaRepository<TicketValidation, UUID> {
}