package com.aloc.aloc.problemtag;

import com.aloc.aloc.global.domain.AuditingTimeEntity;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.tag.Tag;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@AllArgsConstructor
@Builder
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProblemTag extends AuditingTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "problem_id", nullable = false)
	private Problem problem;

	@ManyToOne
	@JoinColumn(name = "tag_id", nullable = false)
	private Tag tag;
}
