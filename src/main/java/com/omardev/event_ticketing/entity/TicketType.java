package com.omardev.event_ticketing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ticket_types")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /*
     * Ticket type name.
     * Example:
     * - Standard
     * - VIP
     */
    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    /*
     * Price for this ticket category.
     */
    @Column(nullable = false)
    private BigDecimal price;

    /*
     * Maximum number of tickets available for this type.
     */
    @Column(nullable = false)
    private Integer quantity;

    /*
     * Remaining available tickets.
     */
    @Column(nullable = false)
    private Integer availableQuantity;

    /*
     * Whether this ticket type is currently active.
     */
    @Column(nullable = false)
    private boolean active = true;

    /*
     * Tickets associated with this ticket type.
     * One ticket type can have multiple tickets.
     */
    @OneToMany(mappedBy = "ticketType", fetch = FetchType.LAZY)
    private List<Ticket> tickets = new ArrayList<>();

    /*
     * Event associated with this ticket type.
     * One event can contain multiple ticket types.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (availableQuantity == null) {
            availableQuantity = quantity;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}