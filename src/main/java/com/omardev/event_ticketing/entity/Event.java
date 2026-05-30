package com.omardev.event_ticketing.entity;

import com.omardev.event_ticketing.enums.EventStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String bannerUrl;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Positive
    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer availableTickets;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventStatus status;

    /*
     * Event organizer.
     * One organizer can manage multiple events.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id")
    private User organizer;

    /*
     * Event attendees.
     * Represents users participating in the event.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_attendees",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> attendees = new ArrayList<>();

    /*
     * Event staff members.
     * Responsible for event operations and ticket validation.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_staff",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> staffMembers = new ArrayList<>();

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<TicketType> ticketTypes = new ArrayList<>();

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<Ticket> tickets = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (status == null) {
            status = EventStatus.DRAFT;
        }

        if (availableTickets == null) {
            availableTickets = capacity;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
