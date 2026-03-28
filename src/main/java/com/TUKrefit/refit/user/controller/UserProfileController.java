package com.TUKrefit.refit.user.controller;

import com.TUKrefit.refit.user.dto.*;
import com.TUKrefit.refit.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me/profile")
@RequiredArgsConstructor
@Tag(name = "UserProfile", description = "내 프로필(UserProfile) 관리")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Operation(summary = "프로필 존재 여부", description = "내 UserProfile 생성 여부를 반환")
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = UserProfileExistsResponse.class))
    )
    @GetMapping("/exists")
    public ResponseEntity<UserProfileExistsResponse> exists() {
        // access token의 subject(userId) 기준 조회
        return ResponseEntity.ok(userProfileService.exists(currentUserId()));
    }

    @Operation(summary = "프로필 조회", description = "내 UserProfile 조회(없으면 404)")
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "USER_PROFILE_NOT_FOUND",
            content = @Content(schema = @Schema(implementation = java.util.Map.class))
    )
    @GetMapping
    public ResponseEntity<UserProfileResponse> get() {
        // 내 프로필 단건 조회
        return ResponseEntity.ok(userProfileService.get(currentUserId()));
    }

    @Operation(summary = "프로필 생성/수정", description = "내 UserProfile upsert(없으면 생성, 있으면 수정)")
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "INVALID_USER_PROFILE",
            content = @Content(schema = @Schema(implementation = java.util.Map.class))
    )
    @PutMapping
    public ResponseEntity<UserProfileResponse> upsert(@Valid @RequestBody UserProfileUpsertRequest req) {
        // 없으면 생성, 있으면 전체 갱신
        return ResponseEntity.ok(userProfileService.upsert(currentUserId(), req));
    }

    private String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // JwtAuthenticationFilter에서 principal로 넣은 userId 사용
        if (auth == null || auth.getPrincipal() == null) return null;
        return auth.getPrincipal().toString();
    }
}
