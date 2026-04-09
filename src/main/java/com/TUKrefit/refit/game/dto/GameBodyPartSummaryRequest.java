package com.TUKrefit.refit.game.dto;

import com.TUKrefit.refit.game.entity.BodyPart;
import com.TUKrefit.refit.game.entity.BodySide;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameBodyPartSummaryRequest {

    @NotNull
    private BodyPart bodyPart; // 부위 코드

    @NotNull
    private BodySide side; // 좌우 코드

    @Min(0) @Max(10)
    private Integer pain0to10; // 통증

    @Min(0) @Max(10)
    private Integer stiffness0to10; // 뻣뻣함

    @Min(0) @Max(10)
    private Integer fatigue0to10; // 피로

    private Boolean swelling; // 부종 여부

    @Size(max = 500)
    private String notes; // 메모

    private Map<String, Object> metrics; // 부위별 측정치 확장 필드
}
