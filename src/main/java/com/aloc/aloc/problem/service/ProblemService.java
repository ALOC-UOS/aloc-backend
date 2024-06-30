package com.aloc.aloc.problem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aloc.aloc.facade.ProblemFacade;
import com.aloc.aloc.problem.dto.response.ProblemResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemService {
	private final ProblemFacade problemFacade;

	public List<ProblemResponseDto> getProblems() {
		return problemFacade.getVisibleProblemsWithSolvingCount();
	}

}
