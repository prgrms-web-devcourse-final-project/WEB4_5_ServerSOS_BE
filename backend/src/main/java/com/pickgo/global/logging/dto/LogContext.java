package com.pickgo.global.logging.dto;

import com.pickgo.domain.log.enums.ActorType;


public record LogContext(
        String requestUri,
        String httpMethod,
        String actorId,
        ActorType actorType) {
}
