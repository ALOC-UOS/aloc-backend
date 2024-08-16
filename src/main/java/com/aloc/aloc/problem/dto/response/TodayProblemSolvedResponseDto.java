package com.aloc.aloc.problem.dto.response;

import com.aloc.aloc.problem.enums.ProblemStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TodayProblemSolvedResponseDto {
	private ProblemStatus solvedStatus;
	private Integer userCoin;
	private Integer obtainCoin;
	private Integer place;
}
