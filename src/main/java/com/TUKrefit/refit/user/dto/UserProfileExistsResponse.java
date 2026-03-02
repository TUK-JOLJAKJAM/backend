package com.TUKrefit.refit.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileExistsResponse {
    private boolean exists;
}