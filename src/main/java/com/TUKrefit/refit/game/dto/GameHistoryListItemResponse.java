package com.TUKrefit.refit.game.dto;

import com.TUKrefit.refit.auth.entity.ClientType;
import com.TUKrefit.refit.game.entity.BodyPart;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameHistoryListItemResponse {
    private String historyId;
    private String gameId;
    private String gameName;
    private String gameVersion;
    private BodyPart primaryPart;
    private ClientType clientType;
    private Long startedAtMs;
    private Long endedAtMs;
    private Long durationMs;
    private Integer score;
    private Integer actionCount;
    private Integer successCount;
    private Integer failCount;
    private Long createdAtMs;
}
