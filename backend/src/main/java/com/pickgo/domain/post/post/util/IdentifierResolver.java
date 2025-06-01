package com.pickgo.domain.post.post.util;

import com.pickgo.domain.member.member.dto.MemberPrincipal;
import com.pickgo.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdentifierResolver {
    private final JwtProvider jwtProvider;

    public String resolve(HttpServletRequest request) {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            String accessToken = jwtProvider.getTokenFromHeader(authorizationHeader);
            Authentication authentication = jwtProvider.getAuthentication(accessToken);
            Object principal = authentication.getPrincipal();
            if (principal instanceof MemberPrincipal(java.util.UUID id)) {
                return "MEMBER_" + id.toString();
            }
        } catch (Exception ignored) {
            
        }

        return "IP_" + request.getRemoteAddr();
    }
}
