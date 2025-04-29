package com.pickgo.domain.example.dto;

import com.pickgo.domain.example.entity.ExampleType;

import jakarta.validation.constraints.NotNull;

public record ExampleUpdateRequest(
	@NotNull String title,
	@NotNull String body,
	@NotNull ExampleType type
) {
}
