package com.pickgo.domain.member.dto;

public record LoginRequest(
    String email,
    String password
) {
}
