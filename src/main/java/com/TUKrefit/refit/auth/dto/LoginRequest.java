package com.TUKrefit.refit.auth.dto;

import com.TUKrefit.refit.auth.entity.ClientType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class LoginRequest {

    @Email
    @NotBlank
    private String email; // 로그인 ID

    @NotBlank
    private String password; // 평문은 요청 본문에서만 사용

    @NotNull
    private ClientType clientType; // UNITY / WEB

    private String deviceId; // 단말 식별자(선택)
}
