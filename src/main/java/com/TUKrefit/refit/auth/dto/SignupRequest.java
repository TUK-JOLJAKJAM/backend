package com.TUKrefit.refit.auth.dto;

import com.TUKrefit.refit.user.entity.Gender;
import com.TUKrefit.refit.user.entity.Role;
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
    private String email; // 로그인 ID

    @NotBlank @Size(min = 8, max = 64)
    private String password; // 서버에서 BCrypt 해시로만 저장

    @NotBlank @Size(max = 50)
    private String name; // 사용자명

    private Role role;   // null이면 PATIENT로 처리
    private Gender gender; // 선택 입력
    private Integer birthYear; // 선택 입력
}
