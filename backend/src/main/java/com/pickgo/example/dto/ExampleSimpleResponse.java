package com.pickgo.example.dto;

import com.pickgo.example.entity.Example;

public record ExampleSimpleResponse(
	Long id,
	String title
) {
	public static ExampleSimpleResponse from(Example example) {
		return new ExampleSimpleResponse(example.getId(), example.getTitle());
	}
}
