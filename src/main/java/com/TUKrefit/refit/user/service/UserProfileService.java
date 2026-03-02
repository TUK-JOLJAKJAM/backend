package com.TUKrefit.refit.user.service;

import com.TUKrefit.refit.auth.entity.User;
import com.TUKrefit.refit.auth.entity.UserProfile;
import com.TUKrefit.refit.auth.exception.AuthErrorCode;
import com.TUKrefit.refit.auth.exception.AuthException;
import com.TUKrefit.refit.auth.repository.UserProfileRepository;
import com.TUKrefit.refit.auth.repository.UserRepository;
import com.TUKrefit.refit.user.dto.UserProfileExistsResponse;
import com.TUKrefit.refit.user.dto.UserProfileResponse;
import com.TUKrefit.refit.user.dto.UserProfileUpsertRequest;
import com.TUKrefit.refit.user.mapper.UserProfileMapper;
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
        validate(req);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.UNAUTHORIZED));

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
        if (req.getDominantHand() != null && !req.getDominantHand().isBlank()) {
            String x = req.getDominantHand().trim().toUpperCase();
            if (!x.equals("L") && !x.equals("R")) {
                throw new AuthException(AuthErrorCode.INVALID_USER_PROFILE);
            }
        }
        if (req.getPainBaseline0to10() != null) {
            int v = req.getPainBaseline0to10();
            if (v < 0 || v > 10) throw new AuthException(AuthErrorCode.INVALID_USER_PROFILE);
        }
    }
}