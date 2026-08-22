package com.omardev.event_ticketing.repository;

import com.omardev.event_ticketing.entity.Event;
import com.omardev.event_ticketing.enums.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    /**
     * Find events by organizer ID (no filter, paginated).
     */
    Page<Event> findByOrganizer_Id(
            UUID organizerId,
            Pageable pageable
    );

    /**
     * Find events by organizer ID with optional status filter (paginated).
     */
    Page<Event> findByOrganizer_IdAndStatus(
            UUID organizerId,
            EventStatus status,
            Pageable pageable
    );


    /**
     * Find all events by status (paginated).
     */
    Page<Event> findByStatus(EventStatus status, Pageable pageable);

    /**
     * Search published events by keyword (title, description, location).
     */
    @Query("""
    SELECT e FROM Event e
    WHERE e.status = com.omardev.event_ticketing.enums.EventStatus.PUBLISHED
    AND (
        LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
""")
    Page<Event> searchPublishedEvents(
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
