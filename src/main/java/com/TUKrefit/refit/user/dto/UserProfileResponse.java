package com.TUKrefit.refit.user.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {

    private String userId;

    private Float heightCm;
    private Float weightKg;
    private String dominantHand;
    private List<String> diagnosisTags;
    private Integer painBaseline0to10;
    private String notes;

    private Long updatedAtMs;
}