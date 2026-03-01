package com.TUKrefit.refit.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor @Builder
public class LogoutResponse {
    private String authId;
    private Long logoutAtMs;
}