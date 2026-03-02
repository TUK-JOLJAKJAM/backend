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

    private static final String CLAIM_TYP = "typ";
    private static final String TYP_ACCESS = "access";
    private static final String TYP_REFRESH = "refresh";

    private final SecretKey key;
    private final long accessExpSeconds;
    private final long refreshExpSeconds;

    public JwtProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-exp-seconds}") long accessExpSeconds,
            @Value("${app.jwt.refresh-token-exp-seconds}") long refreshExpSeconds
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpSeconds = accessExpSeconds;
        this.refreshExpSeconds = refreshExpSeconds;
    }

    public String createAccessToken(String userId, String authId, long issuedAtMs) {
        long expMs = issuedAtMs + accessExpSeconds * 1000L;
        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .subject(userId)
                .id(jti)
                .claims(Map.of(
                        "authId", authId,
                        CLAIM_TYP, TYP_ACCESS
                ))
                .issuedAt(new Date(issuedAtMs))
                .expiration(new Date(expMs))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(String userId, String authId, long issuedAtMs) {
        long expMs = issuedAtMs + refreshExpSeconds * 1000L;
        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .subject(userId)
                .id(jti)
                .claims(Map.of(
                        "authId", authId,
                        CLAIM_TYP, TYP_REFRESH
                ))
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

    public String getTyp(String token) {
        Object v = parse(token).getPayload().get(CLAIM_TYP);
        return (v == null) ? null : v.toString();
    }

    public boolean isAccessToken(String token) {
        return TYP_ACCESS.equals(getTyp(token));
    }

    public boolean isRefreshToken(String token) {
        return TYP_REFRESH.equals(getTyp(token));
    }

    public long getExpMs(String token) {
        Date d = parse(token).getPayload().getExpiration();
        return (d == null) ? 0L : d.getTime();
    }
}