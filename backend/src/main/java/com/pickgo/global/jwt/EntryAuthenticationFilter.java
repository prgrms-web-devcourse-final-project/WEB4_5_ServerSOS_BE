package com.pickgo.global.jwt;

import static com.pickgo.domain.queue.enums.EntryState.*;
import static com.pickgo.domain.queue.repository.redis.RedisEntryRepository.*;

import java.io.IOException;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pickgo.domain.member.member.dto.MemberPrincipal;
import com.pickgo.domain.queue.enums.EntryState;
import com.pickgo.domain.queue.repository.EntryRepository;
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
    private final EntryRepository entryRepository;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 인증할 경로 설정
        if (!(request.getRequestURI().equals("/api/payments") && "POST".equals(request.getMethod())
            || request.getRequestURI().equals("/api/payments/confirm")
            || request.getRequestURI().equals("/api/areas") && "GET".equals(request.getMethod())
            || request.getRequestURI().equals("/api/reservations") && "POST".equals(request.getMethod())
        )) {
            filterChain.doFilter(request, response);
            return;
        }

        UUID userId = getUserIdFromSecurityContext();
        EntryState state = entryRepository.getState(userId);

        // ACTIVE 상태인 경우 통과
        if (state == ACTIVE) {
            filterChain.doFilter(request, response);
            return;
        }

        // 상태가 null이거나 PENDING인 경우 토큰 검증 후 ACTIVE로 전환
        if (isValidToken(request, response)) {
            entryRepository.setState(userId, ACTIVE, MAX_ACTIVE_TIMEOUT_MINUTES);
            filterChain.doFilter(request, response);
        }
    }

    private boolean isValidToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String entryAuthHeader = request.getHeader(HEADER_ENTRY_AUTH);
            String entryToken = jwtProvider.getTokenFromHeader(entryAuthHeader);
            jwtProvider.validateToken(entryToken);
            return true;
        } catch (BusinessException e) {
            jwtAuthenticationEntryPoint.commence(request, response, e);
            return false;
        } catch (AuthenticationException e) {
            jwtAuthenticationEntryPoint.commence(request, response, e);
            return false;
        }
    }

    private UUID getUserIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberPrincipal principal = (MemberPrincipal)authentication.getPrincipal();
        return principal.id();
    }
}
