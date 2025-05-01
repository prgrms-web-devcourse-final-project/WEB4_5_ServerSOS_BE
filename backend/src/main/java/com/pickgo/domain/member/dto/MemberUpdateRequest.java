package com.pickgo.domain.member.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberUpdateRequest(@NotBlank String nickname) {
}
