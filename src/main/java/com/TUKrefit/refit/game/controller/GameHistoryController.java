package com.TUKrefit.refit.game.controller;

import com.TUKrefit.refit.game.dto.*;
import com.TUKrefit.refit.game.service.GameHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/game-histories")
@RequiredArgsConstructor
@Tag(name = "GameHistory", description = "게임 플레이 히스토리 저장/조회")
public class GameHistoryController {

    private final GameHistoryService gameHistoryService;

    @Operation(summary = "게임 히스토리 저장", description = "게임 플레이 종료 후 세션 요약/부위별 요약/게임 데이터 저장")
    @ApiResponse(
            responseCode = "201",
            description = "Created",
            content = @Content(schema = @Schema(implementation = GameHistoryCreateResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "INVALID_GAME_HISTORY",
            content = @Content(schema = @Schema(implementation = java.util.Map.class))
    )
    @PostMapping
    public ResponseEntity<GameHistoryCreateResponse> create(@Valid @RequestBody GameHistoryCreateRequest req) {
        // 유니티 클라이언트는 플레이 종료 시점에 이 API를 호출하면 됨
        GameHistoryCreateResponse response = gameHistoryService.create(currentUserId(), req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "내 게임 히스토리 목록", description = "최신 종료 시각 기준 내림차순 조회")
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = GameHistoryListResponse.class))
    )
    @GetMapping
    public ResponseEntity<GameHistoryListResponse> list(
            @RequestParam(required = false) String gameId,
            @RequestParam(required = false) Long fromMs,
            @RequestParam(required = false) Long toMs,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        return ResponseEntity.ok(gameHistoryService.list(currentUserId(), gameId, fromMs, toMs, page, size));
    }

    @Operation(summary = "내 게임 히스토리 상세", description = "선택한 히스토리의 세부 데이터 조회")
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = GameHistoryDetailResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "GAME_HISTORY_NOT_FOUND",
            content = @Content(schema = @Schema(implementation = java.util.Map.class))
    )
    @GetMapping("/{historyId}")
    public ResponseEntity<GameHistoryDetailResponse> detail(@PathVariable String historyId) {
        return ResponseEntity.ok(gameHistoryService.detail(currentUserId(), historyId));
    }

    private String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) return null;
        return auth.getPrincipal().toString();
    }
}
