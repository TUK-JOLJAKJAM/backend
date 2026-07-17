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

@RestController
@RequestMapping("/api/v1/analyses")
@RequiredArgsConstructor
@Tag(name = "Analysis", description = "사용자 전체 규칙 기반 분석 이력")
@SecurityRequirement(name = "bearerAuth")
public class UserAnalysisController {
    private final AnalysisService analysisService;

    @GetMapping
    @Operation(summary = "내 전체 분석 이력", description = "여러 게임 기록의 최신 분석 흐름을 시간 역순으로 조회")
    public ResponseEntity<AnalysisListResponse> list(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return ResponseEntity.ok(analysisService.listForUser(currentUserId(), page, size));
    }

    private String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) return null;
        if ("anonymousUser".equals(auth.getPrincipal())) return null;
        return auth.getPrincipal().toString();
    }
}
