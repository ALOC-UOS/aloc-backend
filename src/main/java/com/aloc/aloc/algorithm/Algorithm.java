package com.aloc.aloc.algorithm;

import com.aloc.aloc.global.domain.AuditingTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Algorithm extends AuditingTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Integer algorithmId;

	@Column(nullable = false)
	private String name;

	private int season;
	private boolean hidden;
}
