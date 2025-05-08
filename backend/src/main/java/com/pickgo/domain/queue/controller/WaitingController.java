package com.pickgo.domain.queue.controller;

import static com.pickgo.domain.queue.enums.EntryState.*;
import static com.pickgo.global.response.RsCode.*;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.pickgo.domain.member.dto.MemberPrincipal;
import com.pickgo.domain.queue.dto.AccessRequest;
import com.pickgo.domain.queue.dto.WaitingPosition;
import com.pickgo.domain.queue.service.EntryService;
import com.pickgo.domain.queue.service.WaitingService;
import com.pickgo.domain.queue.sse.SseEmitterHandler;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.jwt.JwtProvider;
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
	private final JwtProvider jwtProvider;
	private final EntryService entryService;

	@Operation(summary = "대기열 입장")
	@PostMapping("/enter")
	public RsData<WaitingPosition> enterWaitingLine(@AuthenticationPrincipal MemberPrincipal principal) {
		return RsData.from(SUCCESS, waitingService.enterWaitingLine(principal.id()));
	}

	@Operation(summary = "대기열 상태 구독")
	@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter subscribeWaitingStatus(@AuthenticationPrincipal MemberPrincipal principal) {
		if (!waitingService.isIn(principal.id())) {
			throw new BusinessException(BAD_REQUEST);
		}
		return sseEmitterHandler.subscribe(principal.id());
	}

	@Operation(summary = "리소스 접근(추후 좌석 조회 API로 대체)")
	@PostMapping("/access")
	public RsData<?> access(
		@AuthenticationPrincipal MemberPrincipal principal,
		@RequestBody AccessRequest request
	) throws InterruptedException {
		// 토큰 검증
		jwtProvider.validateToken(request.entryToken());

		// API를 호출했으므로 입장 활성화
		entryService.setState(principal.id(), ACTIVE);

		// 서비스 로직 수행
		Thread.sleep(5000);

		// 입장한 사용자 집합에서 삭제
		entryService.remove(principal.id());

		return RsData.from(SUCCESS);
	}
}
