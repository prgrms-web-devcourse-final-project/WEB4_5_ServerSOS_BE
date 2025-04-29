package com.pickgo.domain.example.dto;

import com.pickgo.domain.example.entity.Example;
import com.pickgo.domain.example.entity.ExampleType;

import jakarta.validation.constraints.NotNull;

public record ExampleCreateRequest(
	@NotNull String title,
	@NotNull String body,
	ExampleType type
) {
	public Example toEntity() {
		return Example.builder()
			.title(title)
			.body(body)
			.type(type)
			.build();
	}
}
