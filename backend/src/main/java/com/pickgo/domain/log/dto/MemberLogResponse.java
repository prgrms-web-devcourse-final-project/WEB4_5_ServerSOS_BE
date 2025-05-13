package com.pickgo.domain.log.dto;

import com.pickgo.domain.log.entity.MemberHistory;
import com.pickgo.domain.member.member.entity.enums.Authority;
import com.pickgo.domain.member.member.entity.enums.SocialProvider;

public record MemberLogResponse(
        String email,
        String nickname,
        Authority authority,
        SocialProvider socialProvider,
        BaseLogResponse base
) {
    public static MemberLogResponse from(MemberHistory h) {
        return new MemberLogResponse(
                h.getEmail(),
                h.getNickname(),
                h.getAuthority(),
                h.getSocialProvider(),
                BaseLogResponse.from(h)
        );
    }
}
