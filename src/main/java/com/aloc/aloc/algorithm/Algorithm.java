package com.aloc.aloc.algorithm;

import com.aloc.aloc.global.domain.AuditingTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Algorithm extends AuditingTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Integer algorithmId;

	@Column(nullable = false)
	private String name;

	private Integer season = 2;
	private Boolean hidden;

	@Builder
	public Algorithm(Integer algorithmId, String name, Integer season, Boolean hidden) {
		this.algorithmId = algorithmId;
		this.name = name;
		this.season = season;
		this.hidden = hidden;
	}

	public void setHiddenFalse() {
		this.hidden = false;
	}
}

