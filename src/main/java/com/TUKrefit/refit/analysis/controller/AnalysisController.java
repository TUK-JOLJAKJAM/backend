package com.TUKrefit.refit.analysis.controller;

import com.TUKrefit.refit.analysis.dto.AnalysisListResponse;
import com.TUKrefit.refit.analysis.service.AnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/game-histories/{historyId}/analyses")
@RequiredArgsConstructor
@Tag(name = "Analysis", description = "게임 기록 규칙 기반 분석 및 결과 저장")
@SecurityRequirement(name = "bearerAuth")
public class AnalysisController {
    private final AnalysisService analysisService;

    @PostMapping
    @Operation(summary = "게임 기록 분석", description = "게임 기록과 사용자 프로필을 분석 서버로 전달하고 결과를 DB에 저장")
    public ResponseEntity<Map<String, Object>> analyze(@PathVariable String historyId) {
        return ResponseEntity.ok(analysisService.analyzeAndSave(currentUserId(), historyId));
    }

    @GetMapping("/latest")
    @Operation(summary = "최신 분석 결과 조회")
    public ResponseEntity<Map<String, Object>> latest(@PathVariable String historyId) {
        return ResponseEntity.ok(analysisService.latest(currentUserId(), historyId));
    }

    @GetMapping
    @Operation(summary = "분석 이력 조회")
    public ResponseEntity<AnalysisListResponse> list(
            @PathVariable String historyId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return ResponseEntity.ok(analysisService.list(currentUserId(), historyId, page, size));
    }

    private String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) return null;
        if ("anonymousUser".equals(auth.getPrincipal())) return null;
        return auth.getPrincipal().toString();
    }
}
