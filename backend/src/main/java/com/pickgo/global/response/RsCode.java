package com.pickgo.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RsCode {

	SUCCESS(RsConstant.SUCCESS, "요청이 성공했습니다."),
	CREATED(RsConstant.CREATED, "새로운 리소스를 생성했습니다."),
	NOT_FOUND(RsConstant.NOT_FOUND, "요청하신 리소스를 찾을 수 없습니다."),
	BAD_REQUEST(RsConstant.BAD_REQUEST, "잘못된 요청입니다."),
	INTERNAL_SERVER(RsConstant.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
	UNAUTHENTICATED(RsConstant.UNAUTHORIZED, "인증이 실패했습니다."),
	UNAUTHORIZED(RsConstant.FORBIDDEN, "접근 권한이 없습니다.");

	private final Integer code;
	private final String message;
}
