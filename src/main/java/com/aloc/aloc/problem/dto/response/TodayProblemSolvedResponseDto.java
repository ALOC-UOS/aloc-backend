package com.aloc.aloc.problem.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TodayProblemSolvedResponseDto {
	private Boolean isSolved;
	private Integer userCoin;
	private Integer obtainCoin;
	private Integer place;
}
