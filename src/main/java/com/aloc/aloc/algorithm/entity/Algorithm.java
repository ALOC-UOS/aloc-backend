package com.aloc.aloc.algorithm.entity;

import com.aloc.aloc.global.domain.AuditingTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@IdClass(AlgorithmPrimaryKey.class)
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Algorithm extends AuditingTimeEntity {
	@Column(nullable = false)
	private Integer week; // 주차

	@Id
	private Integer season;

	@Id
	private Integer algorithmId;

	@Column(nullable = false)
	private String name;

	private Boolean hidden;

	@Builder
	public Algorithm(Integer week, Integer algorithmId, String name, Integer season, Boolean hidden) {
		this.week = week;
		this.season = season;
		this.algorithmId = algorithmId;
		this.name = name;
		this.hidden = hidden;
	}

	public void setHiddenFalse() {
		this.hidden = false;
	}
}

