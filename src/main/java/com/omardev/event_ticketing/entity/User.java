package com.omardev.event_ticketing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /*
     * Reference to the Keycloak user ID (JWT subject claim).
     * Used to synchronize application users with Keycloak identities.
     */
    @Column(nullable = false, unique = true)
    private String keycloakId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    private String phoneNumber;

    private String profilePictureUrl;

    @Column(nullable = false)
    private boolean enabled = true;

    /*
     * Events organized by the user.
     */
    @OneToMany(mappedBy = "organizer", fetch = FetchType.LAZY)
    private List<Event> organizedEvents = new ArrayList<>();

    /*
     * Events attended by the user.
     */
    @ManyToMany(mappedBy = "attendees", fetch = FetchType.LAZY)
    private List<Event> attendedEvents = new ArrayList<>();

    /*
     * Events where the user is part of the staff team.
     */
    @ManyToMany(mappedBy = "staffMembers", fetch = FetchType.LAZY)
    private List<Event> staffedEvents = new ArrayList<>();

    private LocalDateTime createdAt;

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