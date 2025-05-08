package com.pickgo.domain.queue.enums;

public enum EntryState {
	PENDING, ACTIVE;

	public static EntryState from(String value) {
		for (EntryState state : values()) {
			if (state.name().equals(value)) {
				return state;
			}
		}
		return null;
	}
}
