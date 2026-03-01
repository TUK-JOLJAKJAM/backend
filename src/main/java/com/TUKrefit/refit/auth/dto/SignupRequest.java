package com.TUKrefit.refit.auth.dto;

import com.TUKrefit.refit.auth.entity.Gender;
import com.TUKrefit.refit.auth.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank @Size(min = 8, max = 64)
    private String password;

    @NotBlank @Size(max = 50)
    private String name;

    private Role role;   // null이면 PATIENT로 처리
    private Gender gender;
    private Integer birthYear;
}