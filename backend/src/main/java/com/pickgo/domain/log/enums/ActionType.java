package com.pickgo.domain.log.enums;

import lombok.Getter;

@Getter
public enum ActionType {

    // Member
    MEMBER_LOGIN("회원 로그인"),
    MEMBER_LOGIN_KAKAO("카카오 로그인"),
    MEMBER_LOGOUT("회원 로그아웃"),
    MEMBER_SIGNUP("회원 가입"),
    MEMBER_PROFILE("내 정보 조회"),
    MEMBER_DELETED("회원 탈퇴"),

    // Reservation
    RESERVATION_CREATED("예약 생성"),
    RESERVATION_CANCELED("예약 취소"),
    RESERVATION_DELETED("예약 삭제"),
    RESERVATION_EXPIRED("예약 만료"),

    // Payment
    PAYMENT_CREATED("결제 시작"),
    PAYMENT_COMPLETED("결제 완료"),
    PAYMENT_EXPIRED("결제 만료"),
    PAYMENT_FAILED("결제 실패"),
    PAYMENT_CANCELED("결제 취소"),


    // Post
    POST_CREATED("게시글 생성"),
    POST_UPDATED("게시글 수정"),
    POST_DELETED("게시글 삭제"),
    POST_DETAIL_VIEW("게시글 상세 조회"),

    // Exception
    EXCEPTION("예외 발생"),

    // NONE
    NONE("알 수 없음");

    private final String description;

    ActionType(String description) {
        this.description = description;
    }
    }
