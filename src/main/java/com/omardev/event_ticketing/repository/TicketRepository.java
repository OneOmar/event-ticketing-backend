package com.omardev.event_ticketing.repository;

import com.omardev.event_ticketing.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for Ticket persistence.
 */
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    List<Ticket> findByOwnerId(UUID ownerId);

}