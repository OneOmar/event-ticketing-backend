package com.omardev.event_ticketing.repository;

import com.omardev.event_ticketing.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

/**
 * Repository for Ticket persistence.
 */
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    // Standard query (may trigger N+1 on relations)
    List<Ticket> findByOwnerId(UUID ownerId);

    // Optimized query (fetch qrCodes to avoid N+1)
    @Query("""
    SELECT t FROM Ticket t
    LEFT JOIN FETCH t.qrCodes
    WHERE t.owner.id = :ownerId
    """)
    List<Ticket> findByOwnerIdWithQrCodes(UUID ownerId);
}