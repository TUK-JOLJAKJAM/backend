package com.TUKrefit.refit.analysis.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisSummaryResponse {
    private String analysisId;
    private String historyId;
    private String analysisVersion;
    private String schemaVersion;
    private Integer score;
    private String safetyStatus;
    private String difficultyRecommend;
    private String dataQualityStatus;
    private Double completeness;
    private Long analyzedAtMs;
}
