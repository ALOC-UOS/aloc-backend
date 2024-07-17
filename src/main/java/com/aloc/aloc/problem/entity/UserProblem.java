package com.aloc.aloc.problem.entity;

import java.time.LocalDateTime;

import com.aloc.aloc.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProblem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "problem_id")
	private Problem problem;

	private Integer season;

	@Builder.Default
	private Boolean isSolved = false;

	private LocalDateTime solvedAt;

	@Builder
	public UserProblem(User user, Problem problem, Integer season, Boolean isSolved) {
		this.user = user;
		this.problem = problem;
		this.season = season;
		this.isSolved = (isSolved != null) ? isSolved : false;
		this.solvedAt = LocalDateTime.now();
	}
}
