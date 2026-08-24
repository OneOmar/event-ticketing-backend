package com.omardev.event_ticketing.repository;


import com.omardev.event_ticketing.entity.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TicketTypeRepository extends JpaRepository<TicketType, UUID> {
}