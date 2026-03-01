package com.TUKrefit.refit.auth.service;

import com.TUKrefit.refit.auth.dto.*;
import com.TUKrefit.refit.auth.entity.*;
import com.TUKrefit.refit.auth.exception.AuthErrorCode;
import com.TUKrefit.refit.auth.exception.AuthException;
import com.TUKrefit.refit.auth.mapper.UserMapper;
import com.TUKrefit.refit.auth.repository.AuthLogRepository;
import com.TUKrefit.refit.auth.repository.UserRepository;
import com.TUKrefit.refit.auth.security.JwtProvider;
import com.TUKrefit.refit.auth.security.TokenBlacklistService;
import com.TUKrefit.refit.common.util.TimeUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthLogRepository authLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final TokenBlacklistService tokenBlacklistService;

    @Transactional
    public void signup(SignupRequest req) {
        String email = req.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String pwHash = passwordEncoder.encode(req.getPassword());
        User user = UserMapper.toNewUser(req, pwHash);
        userRepository.save(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest req, HttpServletRequest httpReq) {
        User user = userRepository.findByEmail(req.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        long now = TimeUtil.nowMs();
        String authId = UUID.randomUUID().toString();

        AuthLog log = AuthLog.builder()
                .authId(authId)
                .userId(user.getUserId())
                .loginAtMs(now)
                .logoutAtMs(null)
                .clientType(req.getClientType())
                .deviceId(req.getDeviceId())
                .ip(extractIp(httpReq))
                .build();

        authLogRepository.save(log);

        String token = jwtProvider.createAccessToken(user.getUserId(), authId, now);
        long expMs = jwtProvider.getExpMs(token);

        return AuthResponse.builder()
                .userId(user.getUserId())
                .authId(authId)
                .accessToken(token)
                .issuedAtMs(now)
                .expiresAtMs(expMs)
                .build();
    }

    @Transactional
    public LogoutResponse logout(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
        String token = bearerToken.substring(7);

        long now = TimeUtil.nowMs();

        String jti = jwtProvider.getJti(token);
        long expMs = jwtProvider.getExpMs(token);

        long ttlMs = expMs - now;
        tokenBlacklistService.blacklist(jti, ttlMs);

        String authId = jwtProvider.getAuthId(token);
        if (authId != null) {
            authLogRepository.findByAuthIdAndLogoutAtMsIsNull(authId)
                    .ifPresent(log -> log.setLogoutAtMs(now));
        }

        return LogoutResponse.builder()
                .authId(authId)
                .logoutAtMs(now)
                .build();
    }

    private String extractIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
        return req.getRemoteAddr();
    }
}