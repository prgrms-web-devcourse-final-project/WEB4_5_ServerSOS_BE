package com.pickgo.global.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.pickgo.global.exception.jwt.JwtAccessDeniedHandler;
import com.pickgo.global.exception.jwt.JwtAuthenticationEntryPoint;
import com.pickgo.global.jwt.EntryAuthenticationFilter;
import com.pickgo.global.jwt.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final EntryAuthenticationFilter entryAuthenticationFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
                .rememberMe(AbstractHttpConfigurer::disable) // 로그인 기억 기능 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 인증 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // Form 기반 로그인 비활성화
                .anonymous(AbstractHttpConfigurer::disable) // 익명 사용자 처리 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // 권한 검증
                        .anyRequest().permitAll()) // 그 외는 jwt 필터로 인증 검증
                .sessionManagement(configurer ->
                        configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // STATELESS 방식
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // jwt 필터 추가
                .addFilterAfter(entryAuthenticationFilter, JwtAuthenticationFilter.class) // 인증 필터 추가
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(jwtAuthenticationEntryPoint)) // 인증 실패 시 수행할 작업 설정
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.accessDeniedHandler(jwtAccessDeniedHandler)) // 인가 실패 시 수행할 작업 설정
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*")); // 모든 도메인에서의 요청 허용
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*")); // 모든 헤더 사용
        configuration.setAllowCredentials(true); // 헤더에 인증정보 포함 허용
        configuration.setExposedHeaders(List.of("Authorization", "EntryAuth")); // 브라우저가 지정한 헤더를 읽을 수 있음

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 엔드포인트에 대해 CORS 설정 적용

        return source;
    }

    /**
     * 비밀번호를 암호화하는 역할 수행
     **/
    @Bean
    public PasswordEncoder passwordEncoder() {
        // salt(비밀번호마다 고유한 랜덤값)를 사용하여 비밀번호 암호화(해싱)하는 인코더 // 단방향 해싱 함수라서 복호화 불가
        return new BCryptPasswordEncoder();
    }
}
