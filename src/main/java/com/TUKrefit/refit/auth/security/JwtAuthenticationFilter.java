package com.TUKrefit.refit.auth.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {

        // Bearer 헤더가 없으면 익명 요청으로 통과
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        try {
            // access 토큰만 인증에 사용
            if (!jwtProvider.isAccessToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            String userId = jwtProvider.getUserId(token);

            // 블랙리스트 체크
            String jti = jwtProvider.getJti(token);
            if (tokenBlacklistService.isBlacklisted(jti)) {
                filterChain.doFilter(request, response);
                return;
            }

            // principal에 userId를 넣고 SecurityContext에 저장
            var auth = new UsernamePasswordAuthenticationToken(userId, null, List.of());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception ignored) {
            // 토큰 파싱 실패 -> 인증 없이 진행
        }

        filterChain.doFilter(request, response);
    }
}
