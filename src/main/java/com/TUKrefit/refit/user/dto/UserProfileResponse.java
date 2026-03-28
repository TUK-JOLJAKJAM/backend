package com.TUKrefit.refit.user.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {

    private String userId; // users.user_id

    private Float heightCm; // 키(cm)
    private Float weightKg; // 몸무게(kg)
    private String dominantHand; // 우세손(L/R)
    private List<String> diagnosisTags; // 질환 태그 목록
    private Integer painBaseline0to10; // 평소 통증(0~10)
    private String notes; // 메모

    private Long updatedAtMs; // 수정 시각(epoch ms)
}
