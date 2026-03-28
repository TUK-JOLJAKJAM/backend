package com.TUKrefit.refit.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String userId; // users.user_id
    private String authId; // auth_log.auth_id

    private String accessToken; // API 인증용 JWT
    private Long issuedAtMs; // 발급 시각(epoch ms)
    private Long expiresAtMs; // Access 만료 시각(epoch ms)

    private String refreshToken; // 재발급용 JWT
    private Long refreshExpiresAtMs; // Refresh 만료 시각(epoch ms)
}
