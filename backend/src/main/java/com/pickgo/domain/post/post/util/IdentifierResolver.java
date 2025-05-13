package com.pickgo.domain.post.post.util;

import com.pickgo.domain.member.member.dto.MemberPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class IdentifierResolver {
    public static String resolve(MemberPrincipal principal, HttpServletRequest request) {
        if (principal != null) {
            return "MEMBER_" + principal.id();
        } else {
            return "IP_" + request.getRemoteAddr();
        }
    }
}
