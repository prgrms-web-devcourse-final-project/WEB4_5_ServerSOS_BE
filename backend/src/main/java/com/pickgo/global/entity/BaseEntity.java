package com.pickgo.global.entity;

import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
	@CreatedDate
	@Column(name = "created_at", columnDefinition = "datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시'")
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "modified_at", columnDefinition = "datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시'")
	private LocalDateTime modifiedAt;
}
