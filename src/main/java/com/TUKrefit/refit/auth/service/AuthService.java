package com.TUKrefit.refit.auth.service;

import com.TUKrefit.refit.auth.dto.*;
import com.TUKrefit.refit.auth.entity.*;
import com.TUKrefit.refit.auth.exception.AuthErrorCode;
import com.TUKrefit.refit.auth.exception.AuthException;
import com.TUKrefit.refit.auth.mapper.UserMapper;
import com.TUKrefit.refit.auth.repository.AuthLogRepository;
import com.TUKrefit.refit.auth.repository.UserRepository;
import com.TUKrefit.refit.auth.security.JwtProvider;
import com.TUKrefit.refit.auth.security.RefreshTokenService;
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

    // 로그아웃 즉시 무효화 유지(Access 블랙리스트)
    private final TokenBlacklistService tokenBlacklistService;

    // Refresh 저장소(Redis)
    private final RefreshTokenService refreshTokenService;

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

        String accessToken = jwtProvider.createAccessToken(user.getUserId(), authId, now);
        long accessExpMs = jwtProvider.getExpMs(accessToken);

        String refreshToken = jwtProvider.createRefreshToken(user.getUserId(), authId, now);
        long refreshExpMs = jwtProvider.getExpMs(refreshToken);

        // 사용자당 1개 refresh만 유지(최소 구현)
        refreshTokenService.save(user.getUserId(), jwtProvider.getJti(refreshToken), refreshExpMs - now);

        return AuthResponse.builder()
                .userId(user.getUserId())
                .authId(authId)
                .accessToken(accessToken)
                .issuedAtMs(now)
                .expiresAtMs(accessExpMs)
                .refreshToken(refreshToken)
                .refreshExpiresAtMs(refreshExpMs)
                .build();
    }

    @Transactional
    public AuthResponse refresh(TokenRefreshRequest req) {
        String refreshToken = req.getRefreshToken();

        try {
            if (!jwtProvider.isRefreshToken(refreshToken)) {
                throw new AuthException(AuthErrorCode.REFRESH_TOKEN_INVALID);
            }

            String userId = jwtProvider.getUserId(refreshToken);
            String authId = jwtProvider.getAuthId(refreshToken);
            String refreshJti = jwtProvider.getJti(refreshToken);

            // Redis에 저장된 jti와 일치해야 유효(회전/폐기 대응)
            if (!refreshTokenService.matches(userId, refreshJti)) {
                throw new AuthException(AuthErrorCode.REFRESH_TOKEN_INVALID);
            }

            long now = TimeUtil.nowMs();

            // 새 토큰 발급(회전)
            String newAccess = jwtProvider.createAccessToken(userId, authId, now);
            long newAccessExpMs = jwtProvider.getExpMs(newAccess);

            String newRefresh = jwtProvider.createRefreshToken(userId, authId, now);
            long newRefreshExpMs = jwtProvider.getExpMs(newRefresh);

            refreshTokenService.save(userId, jwtProvider.getJti(newRefresh), newRefreshExpMs - now);

            return AuthResponse.builder()
                    .userId(userId)
                    .authId(authId)
                    .accessToken(newAccess)
                    .issuedAtMs(now)
                    .expiresAtMs(newAccessExpMs)
                    .refreshToken(newRefresh)
                    .refreshExpiresAtMs(newRefreshExpMs)
                    .build();

        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_INVALID);
        }
    }

    @Transactional
    public LogoutResponse logout(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }
        String accessToken = bearerToken.substring(7);

        // access 토큰이어야 함
        if (!jwtProvider.isAccessToken(accessToken)) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED);
        }

        long now = TimeUtil.nowMs();

        // Access 블랙리스트(즉시 무효화)
        String accessJti = jwtProvider.getJti(accessToken);
        long expMs = jwtProvider.getExpMs(accessToken);
        tokenBlacklistService.blacklist(accessJti, expMs - now);

        // Refresh 폐기(사용자 기준)
        String userId = jwtProvider.getUserId(accessToken);
        refreshTokenService.delete(userId);

        // auth_log는 로그 기록
        String authId = jwtProvider.getAuthId(accessToken);
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