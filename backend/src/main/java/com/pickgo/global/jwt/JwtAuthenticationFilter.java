package com.pickgo.global.jwt;

import static com.pickgo.global.response.RsCode.*;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.exception.jwt.JwtAuthenticationEntryPoint;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 토큰 검증 및 사용자 인증 정보를 저장하는 필터
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;


    /**
     * 스프링 시큐리티 필터에서 인증 처리
     **/
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 인증하지 않고 통과시킬 경로 설정
        if (request.getRequestURI().equals("/api/tokens")
                || request.getRequestURI().equals("/api/members")
                || request.getRequestURI().equals("/api/members/login")
                || request.getRequestURI().equals("/api/oauth/kakao/login")
                || request.getRequestURI().equals("/api/oauth/kakao/login/redirect")
                || request.getRequestURI().startsWith("/api/members/email")
                || request.getRequestURI().startsWith("/api/oauth")
                || request.getRequestURI().startsWith("/swagger-ui")
                || request.getRequestURI().startsWith("/v3/api-docs")
                || (request.getRequestURI().startsWith("/api/posts") && "GET".equals(request.getMethod()))
                || request.getRequestURI().startsWith("/admin/monitoring")
                || request.getRequestURI().equals("/favicon.ico")
                || request.getRequestURI().startsWith("/admin/monitoring/health")
                || request.getRequestURI().startsWith("/actuator")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 검증
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        String accessToken = jwtProvider.getTokenFromHeader(authorizationHeader);
        if (!jwtProvider.isValidToken(accessToken)) {
            jwtAuthenticationEntryPoint.commence(request, response, new BusinessException(UNAUTHENTICATED));
            return;
        }

        // 사용자 인증 정보 - Principal(신원 ex. 아이디), Credentials(자격 증명 ex. 비밀번호), Authorities(권한)
        Authentication authentication = jwtProvider.getAuthentication(accessToken);
        // SecurityContext에 사용자 인증 정보 저장 -> 언제든 전역에서 접근 가능
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}

