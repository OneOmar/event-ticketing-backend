package com.omardev.event_ticketing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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

    @Column(nullable = false, unique = true)
    private String keycloakId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    private String phoneNumber;

    private String profilePictureUrl;

    private boolean enabled = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /*
     * TODO:
     * Later, this entity will support multiple user types:
     * - Attendees
     * - Organizers
     * - Staff members
     *
     * Consider introducing:
     * - a UserRole/UserType enum
     * - role-based relationships
     * - profile specialization if needed
     */

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
