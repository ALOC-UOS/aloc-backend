package com.aloc.aloc.problem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;
import com.aloc.aloc.problemtype.repository.ProblemTypeRepository;
import com.aloc.aloc.user.dto.response.SolvedUserResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemService {
	private final ProblemFacade problemFacade;
	private final ProblemRepository problemRepository;
	private final ProblemTypeRepository problemTypeRepository;

	public List<ProblemResponseDto> getProblems() {
		return problemFacade.getVisibleProblemsWithSolvingCount();
	}

	public List<SolvedUserResponseDto> getSolvedUserListByProblemId(Long problemId) {
		return problemFacade.getSolvedUserList(problemId);
	}

	public ProblemResponseDto findTodayProblemByCourse(Course course) {
		Long problemTypeId =
			problemTypeRepository.findProblemTypeByCourseAndRoutine(course, Routine.DAILY).getId();
		System.out.println(problemTypeId);
		Problem todayProblem = problemRepository.findLatestPublicProblemByProblemTypeId(problemTypeId);
		if (todayProblem == null) {
			throw new IllegalArgumentException("오늘의 문제가 없습니다.");
		}
		return problemFacade.mapToProblemResponseDto(todayProblem);
	}

}
