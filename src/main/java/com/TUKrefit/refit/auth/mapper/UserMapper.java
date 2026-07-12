package com.TUKrefit.refit.auth.mapper;

import com.TUKrefit.refit.common.util.TimeUtil;
import com.TUKrefit.refit.auth.dto.SignupRequest;
import com.TUKrefit.refit.user.entity.Role;
import com.TUKrefit.refit.user.entity.User;
import com.TUKrefit.refit.user.entity.UserProfile;

import java.util.UUID;

public class UserMapper {

    private UserMapper() {}

    public static User toNewUser(SignupRequest req, String passwordHash) {
        long now = TimeUtil.nowMs();
        // role 미지정 시 기본 권한은 PATIENT
        Role role = (req.getRole() == null) ? Role.PATIENT : req.getRole();

        String userId = UUID.randomUUID().toString();
        User user = User.builder()
                .userId(userId)
                .email(req.getEmail().trim().toLowerCase()) // 로그인 일관성을 위해 소문자 저장
                .passwordHash(passwordHash)
                .name(req.getName().trim())
                .role(role)
                .gender(req.getGender())
                .birthYear(req.getBirthYear())
                .createdAtMs(now)
                .updatedAtMs(now)
                .build();

        // 회원가입 시 빈 프로필을 함께 만들어 조회/수정 흐름을 보장
        user.setProfile(UserProfile.builder()
                .userId(userId)
                .user(user)
                .diagnosisTags("[]")
                .updatedAtMs(now)
                .build());
        return user;
    }
}
