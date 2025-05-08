package com.pickgo.domain.queue.dto;

public record WaitingPosition(
	int position
) {
	public static WaitingPosition of(int position) {
		return new WaitingPosition(position);
	}
}
