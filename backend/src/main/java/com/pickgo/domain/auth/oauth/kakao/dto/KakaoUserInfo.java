package com.pickgo.domain.auth.oauth.kakao.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pickgo.domain.member.member.entity.Member;

import static com.pickgo.domain.member.member.entity.enums.Authority.USER;
import static com.pickgo.domain.member.member.entity.enums.SocialProvider.KAKAO;

public record KakaoUserInfo(
    @JsonProperty("id") Long id,
    @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {
    public Member toEntity(String profile) {
        String profileImage = kakaoAccount().profile().profileImageUrl();
        if (profileImage == null) {
            profileImage = profile;
        }

        return Member.builder()
            .id(UUID.randomUUID())
            .email(kakaoAccount().email())
            .password("")
            .nickname(kakaoAccount().profile().nickname())
            .authority(USER)
            .socialProvider(KAKAO)
            .profile(profileImage)
            .build();
    }

    public record KakaoAccount(
        String email,
        Profile profile
    ) {
        public record Profile(
            String nickname,
            @JsonProperty("profile_image_url") String profileImageUrl
        ) {
        }
    }
}
