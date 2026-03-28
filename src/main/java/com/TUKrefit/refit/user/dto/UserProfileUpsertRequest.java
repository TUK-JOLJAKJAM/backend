package com.TUKrefit.refit.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileUpsertRequest {

    private Float heightCm; // 키(cm)
    private Float weightKg; // 몸무게(kg)

    // "L" 또는 "R"
    @Size(min = 1, max = 1)
    private String dominantHand;

    private List<String> diagnosisTags; // 예: ["stroke","knee_oa"]

    @Min(0) @Max(10)
    private Integer painBaseline0to10;

    @Size(max = 500)
    private String notes; // 메모
}
