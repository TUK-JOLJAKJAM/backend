package com.TUKrefit.refit.game.dto;

import com.TUKrefit.refit.auth.entity.ClientType;
import com.TUKrefit.refit.game.entity.BodyPart;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameHistoryDetailResponse {
    private String historyId;
    private String userId;
    private String gameId;
    private String gameName;
    private String gameVersion;
    private BodyPart primaryPart;
    private ClientType clientType;
    private String deviceId;
    private Long startedAtMs;
    private Long endedAtMs;
    private Long durationMs;
    private Integer score;
    private Integer actionCount;
    private Integer successCount;
    private Integer failCount;
    private Map<String, Object> sessionSummary;
    private List<GameBodyPartSummaryResponse> bodyPartSummaries;
    private List<Map<String, Object>> gameData;
    private Long createdAtMs;
    private Long updatedAtMs;
}
