package com.pickgo.global.exception.jwt;

import static com.pickgo.global.response.RsCode.*;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickgo.global.response.RsData;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 스프링 시큐리티 필터에서 인가 실패 시 예외 처리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws
        IOException {
        log.error("[AccessDeniedException] ex", e);

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        String json = objectMapper.writeValueAsString(RsData.from(UNAUTHORIZED));

        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush(); // WAS가 클라이언트에게 즉시 응답
    }
}
