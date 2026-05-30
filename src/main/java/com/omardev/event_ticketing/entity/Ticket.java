package com.omardev.event_ticketing.entity;

import com.omardev.event_ticketing.enums.QrCodeStatus;
import com.omardev.event_ticketing.enums.TicketStatus;
import com.omardev.event_ticketing.enums.TicketValidationMethod;
import com.omardev.event_ticketing.enums.TicketValidationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
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
     * Validation state at event entrance.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketValidationStatus validationStatus;

    /*
     * Validation method used for check-in.
     */
    @Column(name = "validation_method")
    @Enumerated(EnumType.STRING)
    private TicketValidationMethod validationMethod;

    /*
     * QR code status associated with this ticket.
     */
    @Column(name = "qr_code_status")
    @Enumerated(EnumType.STRING)
    private QrCodeStatus qrCodeStatus;

    /*
     * Date/time when the ticket was validated.
     */
    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    /*
     * User who owns this ticket.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

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

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}