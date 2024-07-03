package com.aloc.aloc.problem.entity;

import java.util.ArrayList;
import java.util.List;

import com.aloc.aloc.algorithm.Algorithm;
import com.aloc.aloc.global.domain.AuditingTimeEntity;
import com.aloc.aloc.problemtag.ProblemTag;
import com.aloc.aloc.problemtype.ProblemType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Problem extends AuditingTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private Integer difficulty;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "algorithm_id")
	private Algorithm algorithm;

	private Boolean hidden = true;

	private Integer problemId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "problem_type_id")
	private ProblemType problemType;

	@OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProblemTag> problemTagList = new ArrayList<>();

	@Builder
	public Problem(
		String title,
		Integer difficulty,
		Algorithm algorithm,
		Integer problemId,
		ProblemType problemType
	) {
		this.title = title;
		this.difficulty = difficulty;
		this.algorithm = algorithm;
		this.problemId = problemId;
		this.problemType = problemType;
	}

	public void addProblemTag(ProblemTag problemTag) {
		problemTagList.add(problemTag);
	}
}
