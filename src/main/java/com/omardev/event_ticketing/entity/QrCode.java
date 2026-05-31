package com.omardev.event_ticketing.entity;

import com.omardev.event_ticketing.enums.QrCodeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "qr_codes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrCode {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /*
     * Unique QR code value/token.
     * Used for ticket scanning and validation.
     */
    @Column(nullable = false, unique = true, length = 1000)
    private String code;

    /*
     * Current QR code lifecycle status.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QrCodeStatus status;

    /*
     * Date/time when the QR code expires.
     */
    private LocalDateTime expiresAt;

    /*
     * Date/time when the QR code was scanned/used.
     */
    private LocalDateTime usedAt;

    /*
     * Ticket associated with this QR code.
     * One ticket can own multiple QR codes over time.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

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
        QrCode qrCode = (QrCode) o;
        return Objects.equals(id, qrCode.id) && Objects.equals(code, qrCode.code) && status == qrCode.status && Objects.equals(expiresAt, qrCode.expiresAt) && Objects.equals(usedAt, qrCode.usedAt) && Objects.equals(createdAt, qrCode.createdAt) && Objects.equals(updatedAt, qrCode.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, status, expiresAt, usedAt, createdAt, updatedAt);
    }
}