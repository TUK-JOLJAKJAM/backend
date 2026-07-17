package com.TUKrefit.refit.analysis.service;

import com.TUKrefit.refit.analysis.client.AiAnalysisClient;
import com.TUKrefit.refit.analysis.dto.AnalysisListResponse;
import com.TUKrefit.refit.analysis.dto.AnalysisSummaryResponse;
import com.TUKrefit.refit.analysis.entity.AnalysisResult;
import com.TUKrefit.refit.analysis.exception.AnalysisException;
import com.TUKrefit.refit.analysis.repository.AnalysisResultRepository;
import com.TUKrefit.refit.common.util.TimeUtil;
import com.TUKrefit.refit.game.dto.GameHistoryDetailResponse;
import com.TUKrefit.refit.game.service.GameHistoryService;
import com.TUKrefit.refit.user.dto.UserProfileResponse;
import com.TUKrefit.refit.user.service.UserProfileService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalysisService {
    private static final int MAX_PAGE_SIZE = 100;

    private final GameHistoryService gameHistoryService;
    private final UserProfileService userProfileService;
    private final AnalysisResultRepository analysisResultRepository;
    private final AiAnalysisClient aiAnalysisClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> analyzeAndSave(String userId, String historyId) {
        GameHistoryDetailResponse history = gameHistoryService.detail(userId, historyId);
        UserProfileResponse profile = userProfileService.get(userId);

        Map<String, Object> input = objectMapper.convertValue(
                history,
                new TypeReference<Map<String, Object>>() {}
        );
        input.put("profile", objectMapper.convertValue(
                profile,
                new TypeReference<Map<String, Object>>() {}
        ));

        String inputJson = writeJson(input);
        Map<String, Object> result = aiAnalysisClient.analyze(input);
        AnalysisResult entity = toEntity(userId, historyId, inputJson, result);
        analysisResultRepository.save(entity);
        return result;
    }

    public Map<String, Object> latest(String userId, String historyId) {
        gameHistoryService.detail(userId, historyId);
        AnalysisResult entity = analysisResultRepository
                .findFirstByHistoryIdAndUserIdOrderByAnalyzedAtMsDesc(historyId, userId)
                .orElseThrow(() -> new AnalysisException(HttpStatus.NOT_FOUND, "ANALYSIS_NOT_FOUND"));
        try {
            return objectMapper.readValue(entity.getResultJson(), new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new AnalysisException(HttpStatus.INTERNAL_SERVER_ERROR, "ANALYSIS_RESULT_INVALID");
        }
    }

    public AnalysisListResponse list(String userId, String historyId, Integer page, Integer size) {
        gameHistoryService.detail(userId, historyId);
        int safePage = page == null || page < 0 ? 0 : page;
        int safeSize = size == null || size <= 0 ? 20 : Math.min(size, MAX_PAGE_SIZE);
        Page<AnalysisResult> result = analysisResultRepository
                .findByHistoryIdAndUserIdOrderByAnalyzedAtMsDesc(
                        historyId,
                        userId,
                        PageRequest.of(safePage, safeSize)
                );
        return AnalysisListResponse.builder()
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .hasNext(result.hasNext())
                .items(result.getContent().stream().map(this::toSummary).toList())
                .build();
    }

    public AnalysisListResponse listForUser(String userId, Integer page, Integer size) {
        if (userId == null || userId.isBlank()) {
            throw new AnalysisException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        }
        int safePage = page == null || page < 0 ? 0 : page;
        int safeSize = size == null || size <= 0 ? 20 : Math.min(size, MAX_PAGE_SIZE);
        Page<AnalysisResult> result = analysisResultRepository.findByUserIdOrderByAnalyzedAtMsDesc(
                userId,
                PageRequest.of(safePage, safeSize)
        );
        return AnalysisListResponse.builder()
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .hasNext(result.hasNext())
                .items(result.getContent().stream().map(this::toSummary).toList())
                .build();
    }

    private AnalysisResult toEntity(
            String userId,
            String historyId,
            String inputJson,
            Map<String, Object> result
    ) {
        Map<String, Object> dataQuality = asMap(result.get("data_quality"));
        return AnalysisResult.builder()
                .analysisId(requiredString(result, "analysis_id"))
                .historyId(historyId)
                .userId(userId)
                .analysisVersion(requiredString(result, "analysis_version"))
                .schemaVersion(stringValue(result.get("schema_version"), "unknown"))
                .inputHash(sha256(inputJson))
                .score(intValue(result.get("score"), 0))
                .safetyStatus(stringValue(result.get("safety_status"), "UNKNOWN"))
                .difficultyRecommend(stringValue(result.get("difficulty_recommend"), "MAINTAIN"))
                .dataQualityStatus(stringValue(dataQuality.get("status"), "INSUFFICIENT"))
                .completeness(doubleValue(dataQuality.get("completeness"), 0.0))
                .resultJson(writeJson(result))
                .analyzedAtMs(longValue(result.get("analyzed_at_ms"), TimeUtil.nowMs()))
                .createdAtMs(TimeUtil.nowMs())
                .build();
    }

    private AnalysisSummaryResponse toSummary(AnalysisResult result) {
        return AnalysisSummaryResponse.builder()
                .analysisId(result.getAnalysisId())
                .historyId(result.getHistoryId())
                .analysisVersion(result.getAnalysisVersion())
                .schemaVersion(result.getSchemaVersion())
                .score(result.getScore())
                .safetyStatus(result.getSafetyStatus())
                .difficultyRecommend(result.getDifficultyRecommend())
                .dataQualityStatus(result.getDataQualityStatus())
                .completeness(result.getCompleteness())
                .analyzedAtMs(result.getAnalyzedAtMs())
                .build();
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new AnalysisException(HttpStatus.INTERNAL_SERVER_ERROR, "ANALYSIS_SERIALIZATION_FAILED");
        }
    }

    private String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new AnalysisException(HttpStatus.INTERNAL_SERVER_ERROR, "ANALYSIS_HASH_FAILED");
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object value) {
        return value instanceof Map<?, ?> ? (Map<String, Object>) value : Map.of();
    }

    private String requiredString(Map<String, Object> values, String key) {
        String value = stringValue(values.get(key), null);
        if (value == null || value.isBlank()) {
            throw new AnalysisException(HttpStatus.BAD_GATEWAY, "AI_ANALYSIS_RESPONSE_INVALID");
        }
        return value;
    }

    private String stringValue(Object value, String fallback) {
        return value == null ? fallback : value.toString();
    }

    private int intValue(Object value, int fallback) {
        return value instanceof Number number ? number.intValue() : fallback;
    }

    private long longValue(Object value, long fallback) {
        return value instanceof Number number ? number.longValue() : fallback;
    }

    private double doubleValue(Object value, double fallback) {
        return value instanceof Number number ? number.doubleValue() : fallback;
    }
}
