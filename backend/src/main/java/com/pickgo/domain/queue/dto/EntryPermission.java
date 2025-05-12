package com.pickgo.domain.queue.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record EntryPermission(
	String entryToken,
	@JsonIgnore Instant createdAt
) {
	public static EntryPermission of(String entryToken) {
		return new EntryPermission(entryToken, Instant.now());
	}
}
