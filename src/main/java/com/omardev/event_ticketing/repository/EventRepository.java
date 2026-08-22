package com.omardev.event_ticketing.repository;

import com.omardev.event_ticketing.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    /**
     * Find events by organizer ID.
     */
    List<Event> findByOrganizer_Id(UUID organizerId);

}
