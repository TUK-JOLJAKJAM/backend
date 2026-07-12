package com.TUKrefit.refit.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    // key: jwt:refresh:auth:{authId}
    private static final String PREFIX = "jwt:refresh:auth:";

    // 기존 JTI 검증과 새 JTI 저장을 한 번에 처리해 동시 재사용을 차단
    private static final DefaultRedisScript<Long> ROTATE_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then "
                    + "redis.call('set', KEYS[1], ARGV[2], 'PX', ARGV[3]); "
                    + "return 1; "
                    + "end; "
                    + "return 0;",
            Long.class
    );

    private final StringRedisTemplate redisTemplate;

    // 로그인 세션별로 refresh 토큰 JTI를 저장
    public void save(String authId, String refreshJti, long ttlMs) {
        if (authId == null || authId.isBlank()) return;
        if (refreshJti == null || refreshJti.isBlank()) return;
        if (ttlMs <= 0) return;
        redisTemplate.opsForValue().set(key(authId), refreshJti, ttlMs, TimeUnit.MILLISECONDS);
    }

    public boolean rotate(String authId, String expectedJti, String newJti, long ttlMs) {
        if (authId == null || authId.isBlank()) return false;
        if (expectedJti == null || expectedJti.isBlank()) return false;
        if (newJti == null || newJti.isBlank()) return false;
        if (ttlMs <= 0) return false;

        Long result = redisTemplate.execute(
                ROTATE_SCRIPT,
                List.of(key(authId)),
                expectedJti,
                newJti,
                Long.toString(ttlMs)
        );
        return Long.valueOf(1L).equals(result);
    }

    public void delete(String authId) {
        if (authId == null || authId.isBlank()) return;
        redisTemplate.delete(key(authId));
    }

    private String key(String authId) {
        return PREFIX + authId;
    }
}
