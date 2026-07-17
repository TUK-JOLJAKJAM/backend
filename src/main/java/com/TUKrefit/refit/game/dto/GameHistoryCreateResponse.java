package com.TUKrefit.refit.game.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameHistoryCreateResponse {
    private String historyId; // 생성된 히스토리 ID
    private String schemaVersion; // 저장된 Unity-Spring 데이터 계약 버전
    private Long createdAtMs; // 저장 시각(epoch ms)
}
