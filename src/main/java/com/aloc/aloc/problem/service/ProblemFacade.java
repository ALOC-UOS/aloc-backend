package com.aloc.aloc.problem.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.service.AlgorithmService;
import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserProblem;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.dto.response.SolvedUserResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemFacade {
	private final ProblemService problemService;
	private final ProblemSolvingService problemSolvingService;
	private final AlgorithmService algorithmService;
	private final ProblemMapper problemMapper;

	@Value("${app.season}")
	private Integer season;


	public String checkSolved(String username) throws IOException {
		User user = problemService.findUser(username);
		// 오늘의 문제를 가져옵니다.
		ProblemResponseDto problem = problemService.findTodayProblemByCourse(user.getCourse());

		// 오늘의 문제가 없으면 오류를 발생시킵니다.
		if (problem == null) {
			throw new IllegalArgumentException("오늘의 문제가 없습니다.");
		}

		// 문제를 풀었는지 확인합니다.
		return problemSolvingService.checkAndUpdateProblemSolved(problem, user);
	}

	public List<SolvedUserResponseDto> getSolvedUserListByProblemId(Long problemId) {
		// 문제가 존재하는지 확인합니다.
		problemService.checkProblemExist(problemId);

		// 문제를 푼 사용자 목록을 가져옵니다.
		List<UserProblem> solvedProblems = problemSolvingService.getSolvedUserListByProblemId(problemId);
		return solvedProblems.stream()
			.map(solvedProblem -> {
				User user = solvedProblem.getUser();
				return problemMapper.mapToSolvedUserResponseDto(user, solvedProblem);
			})
			.collect(Collectors.toList());
	}

	public List<ProblemSolvedResponseDto> getWeeklyCompletionStatus(String username) {
		// 사용자 정보를 가져옵니다.
		User user = problemService.findUser(username);

		// 이번주 Weekly 문제를 가져옵니다.
		Algorithm thisWeekAlgorithm = algorithmService.getAlgorithmBySeason(season)
			.orElseThrow(() -> new RuntimeException("해당 주차 알고리즘이 없습니다."));


		// 사용자에 맞는 문제 타입ID를 가져옵니다.
		Long problemTypeId = problemService.getProblemTypeIdByCourseAndRoutine(user.getCourse(), Routine.WEEKLY);

		// 문제 풀이 현황을 리턴합니다.
		List<Problem> thisWeekProblems = problemService.getProblemsByAlgorithmWeekAndProblemTypeId(
			thisWeekAlgorithm.getWeek(), problemTypeId
		);

		return thisWeekProblems.stream()
			.map(problem -> {
				boolean isSolved = problemSolvingService.isProblemAlreadySolved(user.getId(), problem.getId());
				return problemMapper.mapToProblemSolvedResponseDto(problem, isSolved);
			})
			.collect(Collectors.toList());
	}

	public List<ProblemSolvedResponseDto> getUnsolvedProblemListByUser(String githubId) {
		// 사용자 정보를 가져옵니다.
		User user = problemService.findUser(githubId);

		// 사용자가 푼 문제 ID 목록을 가져옵니다.
		List<Long> solvedProblemIds = problemSolvingService.getSolvedProblemListByUser(user.getId())
			.stream()
			.map(UserProblem::getProblem)
			.map(Problem::getId)
			.collect(Collectors.toList());

		// 사용자가 풀지 않은 문제 목록을 가져옵니다.
		List<Problem> unsolvedProblems = problemService.getUnsolvedProblemsBySolvedProblemIds(
			solvedProblemIds);

		return unsolvedProblems.stream()
			.map(problem -> problemMapper.mapToProblemSolvedResponseDto(problem, false))
			.collect(Collectors.toList());
	}

	public List<ProblemSolvedResponseDto> getSolvedProblemListByUser(String githubId) {
		User user = problemService.findUser(githubId);

		// 현재 시즌 동안 유저가 푼 문제 목록을 가져옵니다.
		List<UserProblem> solvedProblems =
			problemSolvingService.getSolvedProblemListByUser(user.getId());

		return problemMapper.mapSolvedProblemToDtoList(solvedProblems);
	}

	public List<Integer> getThisWeekSolvedCount(Long userId, int algorithmId, int season, Course course) {
		List<ProblemResponseDto> thisWeekProblems =
			problemService.getVisibleProblemsByAlgorithm(season, algorithmId, course);
		int solvedCount =  (int) thisWeekProblems.stream()
			.filter(problem -> problemSolvingService.isProblemAlreadySolved(userId, problem.getId()))
			.count();
		Integer problemCount = thisWeekProblems.size();
		Integer unsolvedCount = thisWeekProblems.size() - solvedCount;
		return List.of(solvedCount, problemCount, unsolvedCount);
	}

	public Boolean getTodayProblemSolved(Long userId, Course course) {
		// 오늘의 문제를 가져옵니다.
		ProblemResponseDto todayProblem = problemService.findTodayProblemByCourse(course);
		return problemSolvingService.isProblemAlreadySolved(userId, todayProblem.getId());
	}
}
