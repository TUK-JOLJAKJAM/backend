package com.TUKrefit.refit.user.service;

import com.TUKrefit.refit.auth.exception.AuthErrorCode;
import com.TUKrefit.refit.auth.exception.AuthException;
import com.TUKrefit.refit.user.dto.UserProfileExistsResponse;
import com.TUKrefit.refit.user.dto.UserProfileResponse;
import com.TUKrefit.refit.user.dto.UserProfileUpsertRequest;
import com.TUKrefit.refit.user.entity.User;
import com.TUKrefit.refit.user.entity.UserProfile;
import com.TUKrefit.refit.user.mapper.UserProfileMapper;
import com.TUKrefit.refit.user.repository.UserProfileRepository;
import com.TUKrefit.refit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional(readOnly = true)
    public UserProfileExistsResponse exists(String userId) {
        // user_profile 존재 여부만 빠르게 확인
        return UserProfileExistsResponse.builder()
                .exists(userProfileRepository.existsById(userId))
                .build();
    }

    @Transactional(readOnly = true)
    public UserProfileResponse get(String userId) {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_PROFILE_NOT_FOUND));
        return UserProfileMapper.toResponse(profile);
    }

    @Transactional
    public UserProfileResponse upsert(String userId, UserProfileUpsertRequest req) {
        // 요청값 범위를 먼저 검증
        validate(req);

        // 인증 사용자 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.UNAUTHORIZED));

        // 있으면 수정, 없으면 생성(upsert)
        return userProfileRepository.findById(userId)
                .map(existing -> {
                    UserProfileMapper.apply(existing, req);
                    return UserProfileMapper.toResponse(existing);
                })
                .orElseGet(() -> {
                    UserProfile created = UserProfileMapper.toNew(user, req);
                    userProfileRepository.save(created);
                    return UserProfileMapper.toResponse(created);
                });
    }

    private void validate(UserProfileUpsertRequest req) {
        // 우세손은 L/R만 허용
        if (req.getDominantHand() != null && !req.getDominantHand().isBlank()) {
            String x = req.getDominantHand().trim().toUpperCase();
            if (!x.equals("L") && !x.equals("R")) {
                throw new AuthException(AuthErrorCode.INVALID_USER_PROFILE);
            }
        }
        // 통증 값은 0~10 범위
        if (req.getPainBaseline0to10() != null) {
            int v = req.getPainBaseline0to10();
            if (v < 0 || v > 10) throw new AuthException(AuthErrorCode.INVALID_USER_PROFILE);
        }
    }
}
