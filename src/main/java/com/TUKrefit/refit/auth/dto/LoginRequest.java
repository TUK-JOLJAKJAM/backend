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
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private ClientType clientType; // UNITY / WEB

    private String deviceId;
}
