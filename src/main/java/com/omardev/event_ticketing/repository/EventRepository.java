package com.omardev.event_ticketing.repository;

import com.omardev.event_ticketing.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
}
