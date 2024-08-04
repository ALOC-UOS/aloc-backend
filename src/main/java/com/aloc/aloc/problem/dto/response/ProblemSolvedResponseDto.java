package com.aloc.aloc.problem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProblemSolvedResponseDto {
	@Schema(description = "문제 ID", example = "746")
	private Long id;
	@Schema(description = "문제 백준 ID", example = "1080")
	private Integer problemId;
	@Schema(description = "문제 제목", example = "A와 B ")
	private String problemTitle;
	@Schema(description = "문제 난이도", example = "8")
	private Integer problemDifficulty;
	@Schema(description = "해결 여부", example = "true")
	private Boolean isSolved;
}
