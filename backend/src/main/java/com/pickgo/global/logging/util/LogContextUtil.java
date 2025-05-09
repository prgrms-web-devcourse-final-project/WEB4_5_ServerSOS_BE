package com.pickgo.global.logging.util;

import com.pickgo.domain.log.enums.ActorType;
import com.pickgo.domain.member.dto.MemberPrincipal;
import com.pickgo.global.logging.dto.LogContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class LogContextUtil {
    public LogContext extract() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String actorId;
        ActorType actorType;

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            // 비로그인 사용자
            actorId = request.getRemoteAddr();
            actorType = ActorType.GUEST;
        } else {
            Object principal = auth.getPrincipal();
            if (principal instanceof MemberPrincipal memberPrincipal) {
                actorId = memberPrincipal.id().toString();
                actorType = auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                        ? ActorType.ADMIN
                        : ActorType.USER;
            } else {
                actorId = "unknown";
                actorType = ActorType.GUEST;
            }
        }

        return new LogContext(
                request.getRequestURI(),
                request.getMethod(),
                actorId,
                actorType
        );
    }
}
