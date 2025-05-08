package com.pickgo.domain.queue.enums;

public enum EntryState {
	PENDING, ACTIVE;

	public static EntryState from(String value) {
		for (EntryState state : values()) {
			if (state.name().equalsIgnoreCase(value)) {
				return state;
			}
		}
		throw new IllegalArgumentException("EntryState 파싱 오류: " + value);
	}
}
