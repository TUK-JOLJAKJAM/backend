package com.TUKrefit.refit.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_profile")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UserProfile {

    @Id
    @Column(name = "user_id", length = 36, nullable = false)
    private String userId; // users.user_id와 동일 PK

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // users와 1:1 식별자 공유

    @Column(name = "height_cm")
    private Float heightCm; // 키(cm)

    @Column(name = "weight_kg")
    private Float weightKg; // 몸무게(kg)

    @Column(name = "dominant_hand", length = 1)
    private String dominantHand; // 우세손(L/R)

    @Column(name = "diagnosis_tags", columnDefinition = "json")
    private String diagnosisTags; // 질환 태그 JSON 배열 문자열

    @Column(name = "pain_baseline_0_10")
    private Integer painBaseline0to10; // 평소 통증(0~10)

    @Column(name = "notes", length = 500)
    private String notes; // 자유 메모

    @Column(name = "updated_at_ms", nullable = false)
    private Long updatedAtMs; // 수정 시각(epoch ms)
}
