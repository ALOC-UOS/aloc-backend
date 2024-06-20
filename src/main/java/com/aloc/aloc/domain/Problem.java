package com.aloc.aloc.domain;

import com.aloc.aloc.common.domain.AuditingTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

public class Problem extends AuditingTimeEntity {
	@Id
	@Column(length = 36)
	private Integer id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String tags;

	@Column(nullable = false)
	private Integer difficulty;

	private Integer algorithmId;

}
