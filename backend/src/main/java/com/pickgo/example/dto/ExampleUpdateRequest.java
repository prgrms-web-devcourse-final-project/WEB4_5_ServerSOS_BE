package com.pickgo.example.dto;

import com.pickgo.example.entity.ExampleType;

import jakarta.validation.constraints.NotNull;

public record ExampleUpdateRequest(
	@NotNull String title,
	@NotNull String body,
	@NotNull ExampleType type
) {
}
