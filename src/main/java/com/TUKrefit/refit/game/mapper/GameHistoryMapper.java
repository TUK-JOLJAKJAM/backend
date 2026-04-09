package com.TUKrefit.refit.game.mapper;

import com.TUKrefit.refit.common.util.TimeUtil;
import com.TUKrefit.refit.game.dto.*;
import com.TUKrefit.refit.game.entity.GameHistory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class GameHistoryMapper {

    private static final ObjectMapper OM = new ObjectMapper();

    private GameHistoryMapper() {}

    public static GameHistory toEntity(String userId, GameHistoryCreateRequest req) {
        long now = TimeUtil.nowMs();
        long durationMs = req.getEndedAtMs() - req.getStartedAtMs();

        return GameHistory.builder()
                .historyId(UUID.randomUUID().toString())
                .userId(userId)
                .gameId(req.getGameId().trim())
                .gameName(trimToNull(req.getGameName()))
                .gameVersion(trimToNull(req.getGameVersion()))
                .primaryPart(req.getPrimaryPart())
                .clientType(req.getClientType())
                .deviceId(trimToNull(req.getDeviceId()))
                .startedAtMs(req.getStartedAtMs())
                .endedAtMs(req.getEndedAtMs())
                .durationMs(durationMs)
                .score(req.getScore())
                .actionCount(req.getActionCount())
                .successCount(req.getSuccessCount())
                .failCount(req.getFailCount())
                .sessionSummaryJson(toJsonObject(req.getSessionSummary()))
                .bodyPartSummariesJson(toJsonArray(req.getBodyPartSummaries()))
                .gameDataJson(toJsonArray(req.getGameData()))
                .createdAtMs(now)
                .updatedAtMs(now)
                .build();
    }

    public static GameHistoryCreateResponse toCreateResponse(GameHistory entity) {
        return GameHistoryCreateResponse.builder()
                .historyId(entity.getHistoryId())
                .createdAtMs(entity.getCreatedAtMs())
                .build();
    }

    public static GameHistoryListItemResponse toListItem(GameHistory entity) {
        return GameHistoryListItemResponse.builder()
                .historyId(entity.getHistoryId())
                .gameId(entity.getGameId())
                .gameName(entity.getGameName())
                .gameVersion(entity.getGameVersion())
                .primaryPart(entity.getPrimaryPart())
                .clientType(entity.getClientType())
                .startedAtMs(entity.getStartedAtMs())
                .endedAtMs(entity.getEndedAtMs())
                .durationMs(entity.getDurationMs())
                .score(entity.getScore())
                .actionCount(entity.getActionCount())
                .successCount(entity.getSuccessCount())
                .failCount(entity.getFailCount())
                .createdAtMs(entity.getCreatedAtMs())
                .build();
    }

    public static GameHistoryDetailResponse toDetail(GameHistory entity) {
        return GameHistoryDetailResponse.builder()
                .historyId(entity.getHistoryId())
                .userId(entity.getUserId())
                .gameId(entity.getGameId())
                .gameName(entity.getGameName())
                .gameVersion(entity.getGameVersion())
                .primaryPart(entity.getPrimaryPart())
                .clientType(entity.getClientType())
                .deviceId(entity.getDeviceId())
                .startedAtMs(entity.getStartedAtMs())
                .endedAtMs(entity.getEndedAtMs())
                .durationMs(entity.getDurationMs())
                .score(entity.getScore())
                .actionCount(entity.getActionCount())
                .successCount(entity.getSuccessCount())
                .failCount(entity.getFailCount())
                .sessionSummary(fromJsonObject(entity.getSessionSummaryJson()))
                .bodyPartSummaries(fromJsonBodyPartSummaries(entity.getBodyPartSummariesJson()))
                .gameData(fromJsonArrayOfMap(entity.getGameDataJson()))
                .createdAtMs(entity.getCreatedAtMs())
                .updatedAtMs(entity.getUpdatedAtMs())
                .build();
    }

    private static String toJsonObject(Map<String, Object> data) {
        try {
            if (data == null) return "{}";
            return OM.writeValueAsString(data);
        } catch (Exception e) {
            return "{}";
        }
    }

    private static String toJsonArray(Object data) {
        try {
            if (data == null) return "[]";
            return OM.writeValueAsString(data);
        } catch (Exception e) {
            return "[]";
        }
    }

    private static Map<String, Object> fromJsonObject(String json) {
        try {
            if (json == null || json.isBlank()) return Collections.emptyMap();
            return OM.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            // 파싱 오류가 나도 조회 API가 실패하지 않도록 빈 객체 반환
            return Collections.emptyMap();
        }
    }

    private static List<Map<String, Object>> fromJsonArrayOfMap(String json) {
        try {
            if (json == null || json.isBlank()) return Collections.emptyList();
            return OM.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private static List<GameBodyPartSummaryResponse> fromJsonBodyPartSummaries(String json) {
        try {
            if (json == null || json.isBlank()) return Collections.emptyList();
            return OM.readValue(json, new TypeReference<List<GameBodyPartSummaryResponse>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private static String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
