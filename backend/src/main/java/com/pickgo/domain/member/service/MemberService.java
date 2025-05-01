package com.pickgo.domain.member.service;

import static com.pickgo.global.response.RsCode.*;

import java.util.UUID;

import com.pickgo.global.s3.AwsS3Uploader;
import com.pickgo.global.s3.S3Uploader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pickgo.domain.auth.service.TokenService;
import com.pickgo.domain.member.dto.LoginRequest;
import com.pickgo.domain.member.dto.LoginResponse;
import com.pickgo.domain.member.dto.MemberCreateRequest;
import com.pickgo.domain.member.dto.MemberDetailResponse;
import com.pickgo.domain.member.dto.MemberPasswordUpdateRequest;
import com.pickgo.domain.member.dto.MemberSimpleResponse;
import com.pickgo.domain.member.dto.MemberUpdateRequest;
import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.member.repository.MemberRepository;
import com.pickgo.global.dto.PageResponse;
import com.pickgo.global.exception.BusinessException;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberService {

    @Value("${custom.member.profile}")
    private String profile;

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final S3Uploader s3Uploader;

    @Transactional
    public MemberDetailResponse save(MemberCreateRequest request) {
        boolean isPresent = memberRepository.findByEmail(request.email()).isPresent();
        if (isPresent) {
            throw new BusinessException(MEMBER_ALREADY_EXISTS);
        }

        Member member = request.toEntity(passwordEncoder, profile);
        member = memberRepository.save(member);

        return MemberDetailResponse.from(member);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        Member member = getEntity(request.email());

		/*
        1. password에 저장된 해시값에서 salt값을 추출해서 SignInRequestDto의 비밀번호를 해싱 (salt: 해시값의 일부)
        2. password에 저장된 해시값에서 비밀번호 부분을 추출 (비밀번호: 해시값의 일부)
        3. password에서 추출한 비밀번호와 해싱된 signInRequestDto의 비밀번호가 일치하는지 확인
        */
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new BusinessException(MEMBER_LOGIN_FAILED);
        }

        tokenService.createRefreshToken(member, response);
        String newAccessToken = tokenService.genAccessToken(member);

        return LoginResponse.of(newAccessToken);
    }

    public void logout(HttpServletResponse response) {
        tokenService.removeRefreshTokenCookie(response);
    }

    @Transactional
    public void delete(UUID id, HttpServletResponse response) {
        Member member = getEntity(id);
        tokenService.removeRefreshTokenCookie(response);
        memberRepository.delete(member);
    }

    @Transactional(readOnly = true)
    public MemberDetailResponse getDetail(UUID id) {
        return MemberDetailResponse.from(getEntity(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<MemberSimpleResponse> getPagedMembers(Pageable pageable) {
        Page<Member> members = memberRepository.findAll(pageable);
        return PageResponse.from(members, MemberSimpleResponse::from);
    }

    @Transactional
    public void updatePassword(UUID id, MemberPasswordUpdateRequest request) {
        Member member = getEntity(id);
        member.setPassword(passwordEncoder.encode(request.password()));
    }

    @Transactional
    public MemberDetailResponse updateMyInfo(UUID id, MemberUpdateRequest request) {
        Member member = getEntity(id);
        member.update(request.nickname());
        return MemberDetailResponse.from(member);
    }

    @Transactional
    public String updateProfileImage(UUID id, MultipartFile image) {
        Member member = getEntity(id);

        // 기존 프로필이 기본 이미지가 아니면 s3에서 삭제
        if (!member.getProfile().equals(profile)) {
            s3Uploader.delete(member.getProfile());
        }

        String imageUrl;
        // 이미지 파일이 비었으면 기본 이미지로 설정하고 아니면 s3에 저장
        if (image.isEmpty()) {
            imageUrl = profile;
        } else {
            imageUrl = s3Uploader.upload(image, "profile");
        }

        member.setProfile(imageUrl);

        return imageUrl;
    }

    private Member getEntity(UUID id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
    }

    private Member getEntity(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
    }
}
