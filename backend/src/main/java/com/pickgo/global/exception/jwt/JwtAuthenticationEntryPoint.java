package com.pickgo.global.exception.jwt;

import static com.pickgo.global.response.RsCode.*;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.response.RsConstant;
import com.pickgo.global.response.RsData;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 스프링 시큐리티 필터에서 인증 실패 시 예외 처리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws
        IOException {
        writeUnauthorizedResponse(response, e);
    }

    public void commence(HttpServletRequest request, HttpServletResponse response, BusinessException e) throws
        IOException {
        writeUnauthorizedResponse(response, e);
    }

    private void writeUnauthorizedResponse(HttpServletResponse response, Exception e) throws IOException {
        log.error("[AuthenticationException] ex", e);

        response.setStatus(RsConstant.UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        String json = objectMapper.writeValueAsString(RsData.from(UNAUTHENTICATED));

        try (PrintWriter writer = response.getWriter()) {
            writer.write(json);
            writer.flush(); // WAS가 클라이언트에게 즉시 응답
        }
    }
}
