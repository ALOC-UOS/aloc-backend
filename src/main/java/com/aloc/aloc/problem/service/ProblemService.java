package com.aloc.aloc.problem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.user.dto.response.SolvedUserResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemService {
	private final ProblemFacade problemFacade;

	public List<ProblemResponseDto> getProblems() {
		return problemFacade.getVisibleProblemsWithSolvingCount();
	}

	public List<SolvedUserResponseDto> getSolvedUserListByProblemId(Long problemId) {
		return problemFacade.getSolvedUserList(problemId);
	}
}
