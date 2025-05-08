package com.pickgo.domain.queue.dto;

public record EntryPermission(
	String entryToken
) {
	public static EntryPermission of(String entryToken) {
		return new EntryPermission(entryToken);
	}
}
