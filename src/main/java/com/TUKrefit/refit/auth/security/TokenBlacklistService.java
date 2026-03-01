package com.TUKrefit.refit.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String PREFIX = "jwt:blacklist:";

    private final StringRedisTemplate redisTemplate;

    public void blacklist(String jti, long ttlMs) {
        if (jti == null || jti.isBlank()) return;
        if (ttlMs <= 0) return; // 이미 만료된 토큰이면 저장할 필요 없음
        redisTemplate.opsForValue().set(PREFIX + jti, "1", ttlMs, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String jti) {
        if (jti == null || jti.isBlank()) return false;
        Boolean exists = redisTemplate.hasKey(PREFIX + jti);
        return exists != null && exists;
    }
}