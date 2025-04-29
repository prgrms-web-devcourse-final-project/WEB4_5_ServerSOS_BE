package com.pickgo.member.dto;

public record LoginRequest(
	String email,
	String password
) {
}
