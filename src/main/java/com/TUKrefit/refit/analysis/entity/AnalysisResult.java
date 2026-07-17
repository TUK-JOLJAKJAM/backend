package com.TUKrefit.refit.analysis.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "analysis_result",
        indexes = {
                @Index(name = "idx_analysis_history_time", columnList = "history_id, analyzed_at_ms"),
                @Index(name = "idx_analysis_user_time", columnList = "user_id, analyzed_at_ms")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisResult {

    @Id
    @Column(name = "analysis_id", length = 36, nullable = false)
    private String analysisId;

    @Column(name = "history_id", length = 36, nullable = false)
    private String historyId;

    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    @Column(name = "analysis_version", length = 30, nullable = false)
    private String analysisVersion;

    @Column(name = "schema_version", length = 20, nullable = false)
    private String schemaVersion;

    @Column(name = "input_hash", length = 64, nullable = false)
    private String inputHash;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "safety_status", length = 20, nullable = false)
    private String safetyStatus;

    @Column(name = "difficulty_recommend", length = 20, nullable = false)
    private String difficultyRecommend;

    @Column(name = "data_quality_status", length = 20, nullable = false)
    private String dataQualityStatus;

    @Column(name = "completeness", nullable = false)
    private Double completeness;

    @Column(name = "result_json", columnDefinition = "json", nullable = false)
    private String resultJson;

    @Column(name = "analyzed_at_ms", nullable = false)
    private Long analyzedAtMs;

    @Column(name = "created_at_ms", nullable = false)
    private Long createdAtMs;
}
