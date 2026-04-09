package com.TUKrefit.refit.game.entity;

import com.TUKrefit.refit.auth.entity.ClientType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "game_history",
        indexes = {
                @Index(name = "idx_game_history_user_ended", columnList = "user_id, ended_at_ms"),
                @Index(name = "idx_game_history_game_id", columnList = "game_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameHistory {

    @Id
    @Column(name = "history_id", length = 36, nullable = false)
    private String historyId; // 히스토리 PK(uuid 문자열)

    @Column(name = "user_id", length = 36, nullable = false)
    private String userId; // 소유 사용자 ID

    @Column(name = "game_id", length = 100, nullable = false)
    private String gameId; // 예: Game_Shoulder_FireWood

    @Column(name = "game_name", length = 100)
    private String gameName; // 표시용 이름(선택)

    @Column(name = "game_version", length = 30)
    private String gameVersion; // 밸런싱 버전(선택)

    @Enumerated(EnumType.STRING)
    @Column(name = "primary_part", length = 20, nullable = false)
    private BodyPart primaryPart; // 주 타깃 부위

    @Enumerated(EnumType.STRING)
    @Column(name = "client_type", length = 10, nullable = false)
    private ClientType clientType; // UNITY/WEB

    @Column(name = "device_id", length = 100)
    private String deviceId; // 단말 식별자(선택)

    @Column(name = "started_at_ms", nullable = false)
    private Long startedAtMs; // 플레이 시작 시각(epoch ms)

    @Column(name = "ended_at_ms", nullable = false)
    private Long endedAtMs; // 플레이 종료 시각(epoch ms)

    @Column(name = "duration_ms", nullable = false)
    private Long durationMs; // 종료-시작

    @Column(name = "score")
    private Integer score; // 게임 점수(선택)

    @Column(name = "action_count")
    private Integer actionCount; // 동작 횟수(선택)

    @Column(name = "success_count")
    private Integer successCount; // 성공 횟수(선택)

    @Column(name = "fail_count")
    private Integer failCount; // 실패 횟수(선택)

    @Column(name = "session_summary_json", columnDefinition = "json", nullable = false)
    private String sessionSummaryJson; // 세션 요약 JSON

    @Column(name = "body_part_summaries_json", columnDefinition = "json", nullable = false)
    private String bodyPartSummariesJson; // 부위별 요약 JSON 배열

    @Column(name = "game_data_json", columnDefinition = "json", nullable = false)
    private String gameDataJson; // 게임별 액션 데이터 JSON 배열

    @Column(name = "created_at_ms", nullable = false)
    private Long createdAtMs; // 업로드 시각(epoch ms)

    @Column(name = "updated_at_ms", nullable = false)
    private Long updatedAtMs; // 수정 시각(epoch ms)
}
