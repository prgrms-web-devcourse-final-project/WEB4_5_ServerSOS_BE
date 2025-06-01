package com.pickgo.domain.auth.oauth.kakao.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.pickgo.domain.auth.oauth.kakao.service.KakaoService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/oauth/kakao")
@RequiredArgsConstructor
@Tag(name = "Kakao API", description = "Kakao API 엔드포인트")
public class KakaoController {

    private final KakaoService kakaoService;

    @GetMapping("/login")
    public RedirectView redirectToKakaoLogin(@RequestParam("state") String origin) {
        return kakaoService.redirectToKakaoLogin(origin);
    }

    @GetMapping("/login/redirect")
    public RedirectView login(
            @RequestParam("code") String code,
            @RequestParam("state") String origin,
            HttpServletResponse response
    ) {
        return kakaoService.login(code, origin, response);
    }
}
