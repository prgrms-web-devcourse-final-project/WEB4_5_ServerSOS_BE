package com.pickgo.example.dto;

import com.pickgo.example.entity.Example;
import com.pickgo.example.entity.ExampleType;

public record ExampleDetailResponse(
	String title,
	String body,
	ExampleType type
) {
	public static ExampleDetailResponse from(Example example) {
		return new ExampleDetailResponse(example.getTitle(), example.getBody(), example.getType());
	}
}
