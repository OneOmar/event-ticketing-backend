package com.omardev.event_ticketing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    @Column(name = "keycloak_id", nullable = false, unique = true)
    private String keycloakId;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "profile_picture_url")
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

    /*
     * Tickets owned by the user.
     */
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
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}