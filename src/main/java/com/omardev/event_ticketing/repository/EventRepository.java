package com.omardev.event_ticketing.repository;

import com.omardev.event_ticketing.entity.Event;
import com.omardev.event_ticketing.enums.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    /**
     * Find events by organizer ID (no filter, paginated).
     */
    Page<Event> findByOrganizerId(
            UUID organizerId,
            Pageable pageable
    );

    /**
     * Find events by organizer ID with optional status filter (paginated).
     */
    Page<Event> findByOrganizerIdAndStatus(
            UUID organizerId,
            EventStatus status,
            Pageable pageable
    );


    /**
     * Find all events by status (paginated).
     */
    Page<Event> findAllByStatus(EventStatus status, Pageable pageable);

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
    Page<Event> findPublishedByKeyword(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    /**
     * Find published events within the optional date range.
     */
    @Query("""
    SELECT e FROM Event e
    WHERE e.status = com.omardev.event_ticketing.enums.EventStatus.PUBLISHED
    AND (:startDate IS NULL OR e.startDate >= :startDate)
    AND (:endDate IS NULL OR e.endDate <= :endDate)
    """)
    Page<Event> findPublishedByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    /**
     * Search published events with a keyword + optional date filter.
     */
    @Query("""
    SELECT e FROM Event e
    WHERE e.status = com.omardev.event_ticketing.enums.EventStatus.PUBLISHED
    AND (
        LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
    AND (:startDate IS NULL OR e.startDate >= :startDate)
    AND (:endDate IS NULL OR e.endDate <= :endDate)
    """)
    Page<Event> findPublishedByKeywordAndDateRange(
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
