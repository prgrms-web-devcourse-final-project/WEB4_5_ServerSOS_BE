package com.pickgo.example.dto;

import com.pickgo.example.entity.Example;
import com.pickgo.example.entity.ExampleType;

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
