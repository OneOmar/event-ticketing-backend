package com.omardev.event_ticketing.entity;

import com.omardev.event_ticketing.enums.TicketValidationMethod;
import com.omardev.event_ticketing.enums.TicketValidationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket_validations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketValidation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private TicketValidationStatus status;

    @Enumerated(EnumType.STRING)
    private TicketValidationMethod method;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @Column(name = "validation_notes")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by")
    private User validatedBy;
}