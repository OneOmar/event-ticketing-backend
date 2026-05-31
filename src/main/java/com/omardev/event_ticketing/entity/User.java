package com.omardev.event_ticketing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<Ticket> tickets = new ArrayList<>();

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return enabled == user.enabled && Objects.equals(id, user.id) && Objects.equals(keycloakId, user.keycloakId) && Objects.equals(email, user.email) && Objects.equals(name, user.name) && Objects.equals(phoneNumber, user.phoneNumber) && Objects.equals(profilePictureUrl, user.profilePictureUrl) && Objects.equals(createdAt, user.createdAt) && Objects.equals(updatedAt, user.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, keycloakId, email, name, phoneNumber, profilePictureUrl, enabled, createdAt, updatedAt);
    }
}