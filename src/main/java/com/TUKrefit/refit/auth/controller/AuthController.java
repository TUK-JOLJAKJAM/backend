package com.TUKrefit.refit.auth.controller;

import com.TUKrefit.refit.auth.dto.*;
import com.TUKrefit.refit.auth.service.AuthService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "회원가입/로그인/로그아웃")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "회원가입",
            description = "email을 로그인 ID로 사용. password는 평문 저장 금지(BCrypt)."
    )
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(
            responseCode = "409",
            description = "EMAIL_ALREADY_EXISTS",
            content = @Content(schema = @Schema(implementation = java.util.Map.class))
    )
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest req) {
        // 회원 기본 정보(users) 생성
        authService.signup(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "로그인", description = "성공 시 Access/Refresh 토큰 발급 + auth_log 기록")
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "INVALID_CREDENTIALS",
            content = @Content(schema = @Schema(implementation = java.util.Map.class))
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest req,
            HttpServletRequest httpReq
    ) {
        // 요청 IP/디바이스 정보를 함께 기록
        return ResponseEntity.ok(authService.login(req, httpReq));
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 Access/Refresh 재발급(회전)")
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "REFRESH_TOKEN_INVALID",
            content = @Content(schema = @Schema(implementation = java.util.Map.class))
    )
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody TokenRefreshRequest req) {
        // refresh 토큰 검증 후 access/refresh 재발급
        return ResponseEntity.ok(authService.refresh(req));
    }

    @Operation(summary = "로그아웃", description = "Authorization: Bearer {accessToken} 필요. Access 블랙리스트 등록 + Refresh 폐기 + auth_log.logout_at_ms 갱신")
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = LogoutResponse.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "UNAUTHORIZED",
            content = @Content(schema = @Schema(implementation = java.util.Map.class))
    )
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        // access 즉시 무효화 + refresh 폐기 + auth_log 종료 시각 기록
        return ResponseEntity.ok(authService.logout(authHeader));
    }
}
