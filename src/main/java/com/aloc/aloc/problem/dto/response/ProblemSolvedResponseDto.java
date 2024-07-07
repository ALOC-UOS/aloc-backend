package com.aloc.aloc.problem.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProblemSolvedResponseDto {
	private Long problemId;
	private String problemTitle;
	private Integer problemDifficulty;
	private Boolean isSolved;
}
