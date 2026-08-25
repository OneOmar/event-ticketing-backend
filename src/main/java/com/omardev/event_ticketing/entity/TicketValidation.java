package com.omardev.event_ticketing.entity;

import com.omardev.event_ticketing.enums.TicketValidationMethod;
import com.omardev.event_ticketing.enums.TicketValidationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;
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

    /**
     * Result of validation (SUCCESS / FAILED)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketValidationStatus status;

    /**
     * How the ticket was validated (QR_SCAN, MANUAL...)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketValidationMethod method;

    /**
     * Timestamp of validation
     */
    @Column(name = "validated_at", nullable = false)
    private LocalDateTime validatedAt;

    /**
     * Optional notes (error reason, extra info...)
     */
    @Column(name = "validation_notes", length = 1000)
    private String notes;

    /**
     * Associated ticket
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    /**
     * Staff user who validated the ticket
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by")
    private User validatedBy;

    /**
     * Set validation timestamp automatically
     */
    @PrePersist
    public void onCreate() {
        this.validatedAt = LocalDateTime.now();
    }

    /**
     * Equality based ONLY on id (JPA best practice)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TicketValidation that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}