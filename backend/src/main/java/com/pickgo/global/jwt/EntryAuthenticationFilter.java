package com.pickgo.global.jwt;

import static com.pickgo.global.response.RsCode.*;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.exception.jwt.JwtAuthenticationEntryPoint;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EntryAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_ENTRY_AUTH = "EntryAuth";
    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 인증할 경로 설정
        if (!(request.getRequestURI().equals("/api/areas") && "GET".equals(request.getMethod())
                || request.getRequestURI().equals("/api/areas/subscribe")
                || (request.getRequestURI().equals("/api/reservations") && "POST".equals(request.getMethod()))
        )) {
            filterChain.doFilter(request, response);
            return;
        }

        // 입장 토큰 가져오기
        String entryAuthHeader = request.getHeader(HEADER_ENTRY_AUTH);
        String entryToken = jwtProvider.getTokenFromHeader(entryAuthHeader);

        // 토큰 검증
        if (!jwtProvider.isValidToken(entryToken)) {
            jwtAuthenticationEntryPoint.commence(request, response, new BusinessException(UNAUTHENTICATED));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
