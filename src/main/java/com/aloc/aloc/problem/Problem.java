package com.aloc.aloc.problem;

import java.util.ArrayList;
import java.util.List;

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
import lombok.Getter;

@Entity
@Getter
public class Problem extends AuditingTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private Integer difficulty;

	private Integer algorithmId;

	private Boolean hidden;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "problem_type_id")
	private ProblemType problemType;

	@OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProblemTag> problemTagList = new ArrayList<>();

	public void addProblemTag(ProblemTag problemTag) {
		problemTagList.add(problemTag);
		problemTag.setProblem(this);
	}
}
