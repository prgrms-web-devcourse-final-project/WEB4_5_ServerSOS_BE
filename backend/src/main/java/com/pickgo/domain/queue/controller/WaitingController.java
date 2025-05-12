package com.pickgo.domain.queue.controller;

import static com.pickgo.global.response.RsCode.*;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.pickgo.domain.member.dto.MemberPrincipal;
import com.pickgo.domain.queue.service.EntryService;
import com.pickgo.domain.queue.service.WaitingService;
import com.pickgo.domain.queue.sse.SseEmitterHandler;
import com.pickgo.global.response.RsData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
@Tag(name = "Queue API", description = "Queue API 엔드포인트")
public class WaitingController {

	private final WaitingService waitingService;
	private final SseEmitterHandler sseEmitterHandler;
	private final EntryService entryService;

	@Operation(summary = "대기열 입장 및 구독")
	@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter subscribeWaitingStatus(@AuthenticationPrincipal MemberPrincipal principal) {
		}
		return sseEmitterHandler.subscribe(principal.id());
	}

	@Operation(summary = "결제 처리 후 퇴장")
	@PostMapping("/exit")
	public RsData<?> exit(@AuthenticationPrincipal MemberPrincipal principal) {
		entryService.remove(principal.id());
		return RsData.from(SUCCESS);
	}
}
