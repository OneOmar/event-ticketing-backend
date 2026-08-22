package com.omardev.event_ticketing.repository;

import com.omardev.event_ticketing.entity.Event;
import com.omardev.event_ticketing.enums.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
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

}
