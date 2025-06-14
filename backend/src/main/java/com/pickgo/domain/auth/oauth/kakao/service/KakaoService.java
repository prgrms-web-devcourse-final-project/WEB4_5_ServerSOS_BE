package com.pickgo.domain.auth.oauth.kakao.service;

import static com.pickgo.domain.member.member.entity.enums.SocialProvider.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import com.pickgo.domain.auth.oauth.kakao.dto.KakaoToken;
import com.pickgo.domain.auth.oauth.kakao.dto.KakaoUserInfo;
import com.pickgo.domain.auth.token.service.TokenService;
import com.pickgo.domain.log.enums.ActionType;
import com.pickgo.domain.member.member.entity.Member;
import com.pickgo.domain.member.member.service.MemberService;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.logging.dto.LogContext;
import com.pickgo.global.logging.util.LogContextUtil;
import com.pickgo.global.logging.util.LogWriter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final RestTemplate restTemplate;
    private final TokenService tokenService;
    private final MemberService memberService;
    private final LogWriter logWriter;
    private final LogContextUtil logContextUtil;

    @Value("${custom.oauth.kakao.redirect-uri}")
    private String redirectUri;
    @Value("${custom.oauth.kakao.api-key}")
    private String apiKey;
    @Value("${custom.oauth.kakao.authorize-uri}")
    private String authorizeUri;
    @Value("${custom.oauth.kakao.token-uri}")
    private String tokenUri;
    @Value("${custom.oauth.kakao.user-info-uri}")
    private String userInfoUri;
    @Value("${custom.member.profile}")
    private String profile;

    public RedirectView redirectToKakaoLogin(String origin) {
        String kakaoAuthUrl = UriComponentsBuilder.fromUriString(authorizeUri)
                .queryParam("client_id", apiKey)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("state", origin)
                .build()
                .toString();

        return new RedirectView(kakaoAuthUrl);
    }

    @Transactional
    public RedirectView login(String code, String origin, HttpServletResponse response) {
        KakaoToken kakaoToken = getToken(code);
        KakaoUserInfo userInfo = getUserInfo(kakaoToken.accessToken());

        Member member = getOrCreateMember(userInfo);
        tokenService.createRefreshToken(member, response);

        String homeUrl = UriComponentsBuilder.fromUriString(origin)
                .build()
                .toString();

        LogContext logContext = logContextUtil.extract();
        logWriter.writeMemberLog(member, ActionType.MEMBER_LOGIN_KAKAO, logContext);

        return new RedirectView(homeUrl);
    }

    private KakaoToken getToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", apiKey);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<KakaoToken> response = restTemplate.postForEntity(
                tokenUri,
                request,
                KakaoToken.class
        );

        return response.getBody();
    }

    private KakaoUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserInfo> response = restTemplate.postForEntity(
                userInfoUri,
                request,
                KakaoUserInfo.class
        );

        return response.getBody();
    }

    private Member getOrCreateMember(KakaoUserInfo userInfo) {
        try {
            Member member = memberService.getEntity(userInfo.kakaoAccount().email());
            member.setSocialProvider(KAKAO);
            return member;
        } catch (BusinessException e) {
            Member newMember = userInfo.toEntity(profile);
            return memberService.saveEntity(newMember);
        }
    }
}
