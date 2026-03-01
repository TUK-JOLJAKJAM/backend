package com.TUKrefit.refit.auth.mapper;

import com.TUKrefit.refit.common.util.TimeUtil;
import com.TUKrefit.refit.auth.dto.SignupRequest;
import com.TUKrefit.refit.auth.entity.Role;
import com.TUKrefit.refit.auth.entity.User;

import java.util.UUID;

public class UserMapper {

    private UserMapper() {}

    public static User toNewUser(SignupRequest req, String passwordHash) {
        long now = TimeUtil.nowMs();
        Role role = (req.getRole() == null) ? Role.PATIENT : req.getRole();

        return User.builder()
                .userId(UUID.randomUUID().toString())
                .email(req.getEmail().trim().toLowerCase())
                .passwordHash(passwordHash)
                .name(req.getName().trim())
                .role(role)
                .gender(req.getGender())
                .birthYear(req.getBirthYear())
                .createdAtMs(now)
                .updatedAtMs(now)
                .build();
    }
}
