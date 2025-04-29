package com.pickgo.domain.member.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberPasswordUpdateRequest(@NotBlank String password) {
}
