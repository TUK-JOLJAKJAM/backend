package com.TUKrefit.refit.game.dto;

import com.TUKrefit.refit.game.entity.BodyPart;
import com.TUKrefit.refit.game.entity.BodySide;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameBodyPartSummaryResponse {
    private BodyPart bodyPart;
    private BodySide side;
    private Integer pain0to10;
    private Integer stiffness0to10;
    private Integer fatigue0to10;
    private Boolean swelling;
    private String notes;
    private Map<String, Object> metrics;
}
