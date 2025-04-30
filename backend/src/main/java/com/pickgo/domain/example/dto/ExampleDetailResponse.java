package com.pickgo.domain.example.dto;

import com.pickgo.domain.example.entity.Example;
import com.pickgo.domain.example.entity.ExampleType;

public record ExampleDetailResponse(
	String title,
	String body,
	ExampleType type
) {
	public static ExampleDetailResponse from(Example example) {
		return new ExampleDetailResponse(example.getTitle(), example.getBody(), example.getType());
	}
}
