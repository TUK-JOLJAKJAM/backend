package com.TUKrefit.refit.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenRefreshRequest {

    @NotBlank
    private String refreshToken; // 로그인/재발급에서 받은 refresh token
}
