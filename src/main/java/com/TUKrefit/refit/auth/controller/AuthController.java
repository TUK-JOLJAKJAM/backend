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
        authService.signup(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "로그인", description = "성공 시 JWT(access token) 발급 + auth_log 기록")
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
        return ResponseEntity.ok(authService.login(req, httpReq));
    }

    @Operation(summary = "로그아웃", description = "Authorization: Bearer {token} 필요. Redis 블랙리스트 등록 + auth_log.logout_at_ms 갱신")
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = LogoutResponse.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "UNAUTHORIZED/AUTH_LOG_NOT_FOUND",
            content = @Content(schema = @Schema(implementation = java.util.Map.class))
    )
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        return ResponseEntity.ok(authService.logout(authHeader));
    }
}