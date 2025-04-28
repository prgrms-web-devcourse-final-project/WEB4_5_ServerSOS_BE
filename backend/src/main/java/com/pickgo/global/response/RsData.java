package com.pickgo.global.response;

import lombok.Builder;

@Builder
public record RsData<T>(
	Integer code,
	String message,
	T data
) {
	public static RsData<?> from(RsCode rsCode) {
		return RsData.builder()
			.code(rsCode.getCode())
			.message(rsCode.getMessage())
			.build();
	}

	public static <T> RsData<T> from(RsCode rsCode, T data) {
		return new RsData<>(rsCode.getCode(), rsCode.getMessage(), data);
	}
}
