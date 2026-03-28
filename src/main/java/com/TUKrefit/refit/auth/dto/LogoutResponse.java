package com.TUKrefit.refit.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor @Builder
public class LogoutResponse {
    private String authId; // 종료된 로그인 세션 ID
    private Long logoutAtMs; // 로그아웃 시각(epoch ms)
}
