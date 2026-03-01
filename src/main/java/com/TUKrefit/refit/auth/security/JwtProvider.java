package com.TUKrefit.refit.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtProvider {

    private final SecretKey key;
    private final long expSeconds;

    public JwtProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-exp-seconds}") long expSeconds
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expSeconds = expSeconds;
    }

    public String createAccessToken(String userId, String authId, long issuedAtMs) {
        long expMs = issuedAtMs + expSeconds * 1000L;
        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .subject(userId)
                .id(jti) // 표준 클레임 jti
                .claims(Map.of("authId", authId))
                .issuedAt(new Date(issuedAtMs))
                .expiration(new Date(expMs))
                .signWith(key)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }

    public String getUserId(String token) {
        return parse(token).getPayload().getSubject();
    }

    public String getAuthId(String token) {
        Object v = parse(token).getPayload().get("authId");
        return (v == null) ? null : v.toString();
    }

    public String getJti(String token) {
        return parse(token).getPayload().getId();
    }

    public long getExpMs(String token) {
        Date d = parse(token).getPayload().getExpiration();
        return (d == null) ? 0L : d.getTime();
    }

    public long getIatMs(String token) {
        Date d = parse(token).getPayload().getIssuedAt();
        return (d == null) ? 0L : d.getTime();
    }
}