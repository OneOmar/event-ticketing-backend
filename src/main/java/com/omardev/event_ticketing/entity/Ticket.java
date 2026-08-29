package com.omardev.event_ticketing.entity;

import com.omardev.event_ticketing.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "tickets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /*
     * Unique ticket reference visible to users.
     */
    @Column(name = "ticket_code", nullable = false, unique = true)
    private String ticketCode;

    /*
     * Current ticket lifecycle status.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    /*
     * QR codes associated with this ticket.
     * One ticket can have multiple QR codes.
     */
    @OneToMany(
            mappedBy = "ticket",
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @OrderBy("createdAt DESC")
    private List<QrCode> qrCodes = new ArrayList<>();

    /*
     * Event associated with this ticket.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    /*
     * Ticket category/type.
     * Example:
     * - VIP
     * - Standard
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_type_id", nullable = false)
    private TicketType ticketType;

    /*
     * User who owns this ticket.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /*
     * Ticket validation records.
     * Example:
     * - Ticket scanned by a user
     * - Ticket validated by an admin
     * - Ticket validated by a staff member
     */
    @OneToMany(
            mappedBy = "ticket",
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<TicketValidation> ticketValidations = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ticket ticket = (Ticket) o;
        return id != null && Objects.equals(id, ticket.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}