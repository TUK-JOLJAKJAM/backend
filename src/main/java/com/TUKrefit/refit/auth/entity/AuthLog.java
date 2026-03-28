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
    private String authId; // auth_session PK

    @Column(name = "user_id", length = 36, nullable = false)
    private String userId; // users.user_id

    @Column(name = "login_at_ms", nullable = false)
    private Long loginAtMs; // 로그인 시각(epoch ms)

    @Column(name = "logout_at_ms")
    private Long logoutAtMs; // 로그아웃 시각(epoch ms), 미로그아웃이면 null

    @Enumerated(EnumType.STRING)
    @Column(name = "client_type", nullable = false, length = 10)
    private ClientType clientType; // UNITY/WEB

    @Column(name = "device_id", length = 100)
    private String deviceId; // 단말 식별자

    @Column(name = "ip", length = 45)
    private String ip; // 요청 원본 IP
}
