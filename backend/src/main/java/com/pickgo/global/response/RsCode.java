package com.pickgo.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RsCode {

	// Common
	FORBIDDEN(RsConstant.FORBIDDEN,"접근 권한이 없습니다."),
	SUCCESS(RsConstant.SUCCESS, "요청이 성공했습니다."),
	CREATED(RsConstant.CREATED, "새로운 리소스를 생성했습니다."),
	NOT_FOUND(RsConstant.NOT_FOUND, "요청하신 리소스를 찾을 수 없습니다."),
	BAD_REQUEST(RsConstant.BAD_REQUEST, "잘못된 요청입니다."),
	INTERNAL_SERVER(RsConstant.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
	UNAUTHENTICATED(RsConstant.UNAUTHORIZED, "인증이 실패했습니다."),
	UNAUTHORIZED(RsConstant.FORBIDDEN, "접근 권한이 없습니다."),

	// Member
	MEMBER_LOGIN_FAILED(RsConstant.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다."),
	MEMBER_NOT_FOUND(RsConstant.NOT_FOUND, "존재하지 않는 유저입니다."),
	MEMBER_ALREADY_EXISTS(RsConstant.BAD_REQUEST, "이미 존재하는 유저입니다."),

	// Performance
	PERFORMANCE_SESSION_NOT_FOUND(RsConstant.NOT_FOUND, "존재하지 않는 공연 회차입니다."),

	// RESERVATION
	RESERVATION_NOT_FOUND(RsConstant.NOT_FOUND, "존재하지 않는 예약 내역입니다."),
	RESERVATION_CANCEL(RsConstant.SUCCESS, "예매가 취소되었습니다.");

	private final Integer code;
	private final String message;
}
