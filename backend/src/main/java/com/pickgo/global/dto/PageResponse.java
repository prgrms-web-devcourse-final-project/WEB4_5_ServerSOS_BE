package com.pickgo.global.dto;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

import lombok.Builder;

@Builder
public record PageResponse<T>(
	List<T> items,
	int page,
	int size,
	int totalPages,
	int totalElements
) {
	public static <T, R> PageResponse<R> from(Page<T> page, Function<T, R> mapper) {
		List<R> items = page.getContent().stream()
			.map(mapper)
			.toList();

		return PageResponse.<R>builder()
			.items(items)
			.page(page.getNumber() + 1) // 1부터 시작 보정
			.size(page.getSize())
			.totalPages(page.getTotalPages())
			.totalElements((int)page.getTotalElements())
			.build();
	}
}
