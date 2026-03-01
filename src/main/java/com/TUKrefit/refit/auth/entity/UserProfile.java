package com.TUKrefit.refit.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_profile")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UserProfile {

    @Id
    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "height_cm")
    private Float heightCm;

    @Column(name = "weight_kg")
    private Float weightKg;

    @Column(name = "dominant_hand", length = 1)
    private String dominantHand; // "L" or "R"

    @Column(name = "diagnosis_tags", columnDefinition = "json")
    private String diagnosisTags; // JSON array string

    @Column(name = "pain_baseline_0_10")
    private Integer painBaseline0to10;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "updated_at_ms", nullable = false)
    private Long updatedAtMs;
}