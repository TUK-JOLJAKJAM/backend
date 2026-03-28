package com.TUKrefit.refit.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    // key: jwt:refresh:user:{userId}
    private static final String PREFIX = "jwt:refresh:user:";

    private final StringRedisTemplate redisTemplate;

    // 사용자당 1개 refresh 토큰(jti)만 유지
    public void save(String userId, String refreshJti, long ttlMs) {
        if (userId == null || userId.isBlank()) return;
        if (refreshJti == null || refreshJti.isBlank()) return;
        if (ttlMs <= 0) return;
        redisTemplate.opsForValue().set(PREFIX + userId, refreshJti, ttlMs, TimeUnit.MILLISECONDS);
    }

    public boolean matches(String userId, String refreshJti) {
        String stored = redisTemplate.opsForValue().get(PREFIX + userId);
        return stored != null && stored.equals(refreshJti);
    }

    public void delete(String userId) {
        if (userId == null || userId.isBlank()) return;
        redisTemplate.delete(PREFIX + userId);
    }
}
