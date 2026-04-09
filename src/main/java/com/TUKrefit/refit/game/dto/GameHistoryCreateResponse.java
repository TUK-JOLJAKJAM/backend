package com.TUKrefit.refit.game.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameHistoryCreateResponse {
    private String historyId; // 생성된 히스토리 ID
    private Long createdAtMs; // 저장 시각(epoch ms)
}
