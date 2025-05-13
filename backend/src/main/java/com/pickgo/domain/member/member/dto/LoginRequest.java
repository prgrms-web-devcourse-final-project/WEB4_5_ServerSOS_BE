package com.pickgo.domain.member.member.dto;

public record LoginRequest(
    String email,
    String password
) {
}
