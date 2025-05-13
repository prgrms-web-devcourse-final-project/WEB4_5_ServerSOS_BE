package com.pickgo.domain.log.entity;

import com.pickgo.domain.log.enums.ActionType;
import com.pickgo.domain.log.enums.ActorType;
import com.pickgo.domain.member.member.entity.enums.Authority;
import com.pickgo.domain.member.member.entity.enums.SocialProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberHistory extends BaseLog {

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialProvider socialProvider;

    public MemberHistory(
            String email,
            String nickname,
            Authority authority,
            SocialProvider socialProvider,
            String actorId,
            ActorType actorType,
            ActionType action,
            String requestUri,
            String httpMethod,
            String description
    ) {
        super(actorId, actorType, action, requestUri, httpMethod, description);
        this.email = email;
        this.nickname = nickname;
        this.authority = authority;
        this.socialProvider = socialProvider;
    }
}
