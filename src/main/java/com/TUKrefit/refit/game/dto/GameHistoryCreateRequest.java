package com.TUKrefit.refit.game.dto;

import com.TUKrefit.refit.auth.entity.ClientType;
import com.TUKrefit.refit.game.entity.BodyPart;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameHistoryCreateRequest {

    @NotBlank
    @Size(max = 100)
    private String gameId; // 예: Game_Shoulder_FireWood

    @Size(max = 100)
    private String gameName; // 표시명(선택)

    @Size(max = 30)
    private String gameVersion; // 버전(선택)

    @NotNull
    private BodyPart primaryPart; // 주 타깃 부위

    @NotNull
    private ClientType clientType; // UNITY/WEB

    @Size(max = 100)
    private String deviceId; // 단말 식별자(선택)

    @NotNull
    @PositiveOrZero
    private Long startedAtMs; // 시작 시각(epoch ms)

    @NotNull
    @PositiveOrZero
    private Long endedAtMs; // 종료 시각(epoch ms)

    @Min(0)
    private Integer score; // 점수(선택)

    @Min(0)
    private Integer actionCount; // 수행 동작 수(선택)

    @Min(0)
    private Integer successCount; // 성공 동작 수(선택)

    @Min(0)
    private Integer failCount; // 실패 동작 수(선택)

    private Map<String, Object> sessionSummary; // 세션 요약

    @Valid
    @Size(max = 16)
    private List<GameBodyPartSummaryRequest> bodyPartSummaries; // 부위별 요약

    @Size(max = 5000)
    private List<Map<String, Object>> gameData; // 게임별 액션 데이터
}
