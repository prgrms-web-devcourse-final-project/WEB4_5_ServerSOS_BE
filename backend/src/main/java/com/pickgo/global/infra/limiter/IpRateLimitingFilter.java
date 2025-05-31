package com.pickgo.global.infra.limiter;

import static com.pickgo.global.response.RsCode.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.pickgo.global.response.RsConstant;
import com.pickgo.global.response.RsData;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * IP 기반 요청 제한 필터
 * - 지정된 경로에 대해 IP별 요청 횟수를 제한
 */
@Component
@RequiredArgsConstructor
@Order(1)
public class IpRateLimitingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private static final int EXPIRE_MINUTES = 10; // 캐시 만료 시간
    private static final int MAXIMUM_SIZE = 100_000; // 캐시 용량
    private static final int TRIAL_LIMIT = 5; // 주기당 요청 가능 횟수
    private static final int INTERVAL_SECONDS = 10; // 요청 주기

    /**
     * IP별 요청 제한 버킷 캐시
     * - 10분간 요청 없으면 캐시 자동 제거
     * - 최대 10만 개 IP까지 저장
     */
    private final Cache<String, Bucket> bucketCache = Caffeine.newBuilder()
            .expireAfterAccess(EXPIRE_MINUTES, TimeUnit.MINUTES)
            .maximumSize(MAXIMUM_SIZE)
            .build();

    /**
     * 요청 필터 처리
     * - 제한 대상 경로인지 확인 후 IP 기준으로 버킷에서 토큰 소비
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 제한 대상 경로 필터링
        if (!(request.getRequestURI().equals("/api/areas")
                || request.getRequestURI().equals("/api/areas/subscribe")
        )) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = getClientIp(request);

        // IP별 버킷 조회 또는 생성 (없으면 자동 생성 후 캐시에 등록)
        Bucket bucket = bucketCache.get(ip, k -> createNewBucket());

        // 토큰이 없으면 요청 제한 (429 응답 반환)
        if (!bucket.tryConsume(1)) {
            response.setStatus(RsConstant.TOO_MANY_REQUESTS);
            response.setContentType("application/json;charset=UTF-8");
            String json = objectMapper.writeValueAsString(RsData.from(TOO_MANY_REQUESTS));

            PrintWriter writer = response.getWriter();
            writer.write(json);
            writer.flush(); // 클라이언트에게 즉시 응답 전송
            return;
        }

        // 토큰이 있으면 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    /**
     * 새 버킷 생성
     */
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(TRIAL_LIMIT) // 최대 요청 가능 횟수
                .refillIntervally(TRIAL_LIMIT, Duration.ofSeconds(INTERVAL_SECONDS)) // 요청 가능 횟수 리필
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * 클라이언트 IP 추출
     * - 프록시 환경 고려하여 X-Forwarded-For 헤더 우선 사용
     */
    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        return xfHeader != null ? xfHeader.split(",")[0] : request.getRemoteAddr();
    }
}
