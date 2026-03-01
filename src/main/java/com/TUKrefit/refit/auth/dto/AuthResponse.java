package com.TUKrefit.refit.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor @Builder
public class AuthResponse {
    private String userId;
    private String authId;
    private String accessToken;
    private Long issuedAtMs;
    private Long expiresAtMs;
}
