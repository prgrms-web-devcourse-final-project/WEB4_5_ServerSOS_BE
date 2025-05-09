package com.pickgo.domain.log.entity;

import com.pickgo.domain.log.enums.ActionType;
import com.pickgo.domain.log.enums.ActorType;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExceptionLog extends BaseLog {
    private String exception;

    public ExceptionLog(
            String exception,
            String actorId,
            ActorType actorType,
            ActionType action,
            String requestUri,
            String httpMethod,
            String description
    ) {
        super(actorId, actorType, action, requestUri, httpMethod, description);
        this.exception = exception;
    }
}
