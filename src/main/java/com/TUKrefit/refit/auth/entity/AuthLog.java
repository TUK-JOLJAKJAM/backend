package com.TUKrefit.refit.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "auth_log",
        indexes = {
                @Index(name = "idx_auth_log_user", columnList = "user_id"),
                @Index(name = "idx_auth_log_logout", columnList = "logout_at_ms")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AuthLog {

    @Id
    @Column(name = "auth_id", length = 36, nullable = false)
    private String authId;

    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    @Column(name = "login_at_ms", nullable = false)
    private Long loginAtMs;

    @Column(name = "logout_at_ms")
    private Long logoutAtMs; // nullable

    @Enumerated(EnumType.STRING)
    @Column(name = "client_type", nullable = false, length = 10)
    private ClientType clientType;

    @Column(name = "device_id", length = 100)
    private String deviceId;

    @Column(name = "ip", length = 45)
    private String ip;
}