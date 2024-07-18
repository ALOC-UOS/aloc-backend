package com.aloc.aloc.problem.service;

import java.io.IOException;
import java.util.Arrays;
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
	private Integer currentSeason;


	public String checkSolved(String username) throws IOException {
		User user = problemService.findUser(username);
		// 오늘의 문제를 가져옵니다.
		ProblemResponseDto problem = problemService.findTodayProblemByCourse(user.getCourse());

		// 오늘의 문제가 없으면 오류를 발생시킵니다.
		if (problem == null) {
			throw new IllegalArgumentException("오늘의 문제가 없습니다.");
		}

		// 문제를 풀었는지 확인합니다.
		// TODO: 문제를 풀었는지 확인하는 로직을 구현합니다.
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

	// 이번 주차 문제 가져오는 공통 메소드
	private List<Problem> getThisWeekProblems(User user) {
		Algorithm thisWeekAlgorithm = algorithmService.getAlgorithmBySeason(currentSeason)
			.orElseThrow(() -> new RuntimeException("해당 주차 알고리즘이 없습니다."));
		return problemService.getVisibleDailyProblemsByAlgorithmId(
			currentSeason, thisWeekAlgorithm.getAlgorithmId(), user.getCourse()
		);
	}

	public List<ProblemSolvedResponseDto> getWeeklyCompletionStatus(String username) {
		// 사용자 정보를 가져옵니다.
		User user = problemService.findUser(username);

		// 이번 주차 문제를 가져옵니다.
		List<Problem> thisWeekProblems = getThisWeekProblems(user);

		// 문제 풀이 현황을 리턴합니다.
		return thisWeekProblems.stream()
			.map(problem -> {
				boolean isSolved = problemSolvingService.isProblemAlreadySolved(user.getId(), problem.getId());
				return problemMapper.mapToProblemSolvedResponseDto(problem, isSolved);
			})
			.collect(Collectors.toList());
	}

	public List<ProblemSolvedResponseDto> getUnsolvedProblemListByUser(
		String githubId, Integer season, Routine routine
	) {
		// 사용자 정보를 가져옵니다.
		User user = problemService.findUser(githubId);
		List<UserProblem> unsolvedProblems;
		unsolvedProblems = problemSolvingService.getUserProblemList(user.getId(), season, false, routine);

		return unsolvedProblems.stream()
			.map(problemMapper::mapToProblemSolvedResponseDto)
			.collect(Collectors.toList());
	}

	public List<ProblemSolvedResponseDto> getSolvedProblemListByUser(String githubId, Integer season, Routine routine) {
		User user = problemService.findUser(githubId);

		List<UserProblem> solvedProblems;
		// 현재 시즌 동안 유저가 푼 문제 목록을 가져옵니다.
		solvedProblems = problemSolvingService.getUserProblemList(user.getId(), season, true, routine);
		return problemMapper.mapSolvedProblemToDtoList(solvedProblems);
	}

	public List<Integer> getThisWeekSolvedCount(User user) {
		// 이번 주차 문제를 가져옵니다.
		List<Problem> thisWeekProblems = getThisWeekProblems(user);
		System.out.println("thisWeekProblems: " + thisWeekProblems);
		// 이번 주차 문제 중 푼 문제 수, 문제 수, 안 푼 문제 수를 가져옵니다.
		long solvedCount = thisWeekProblems.stream()
			.filter(problem -> problemSolvingService.isProblemAlreadySolved(user.getId(), problem.getId()))
			.count();
		int totalProblems = thisWeekProblems.size();
		int unsolvedCount = totalProblems - Math.toIntExact(solvedCount);

		// List에 결과를 담아 반환
		return Arrays.asList(Math.toIntExact(solvedCount), totalProblems, unsolvedCount);
	}

	public Boolean getTodayProblemSolved(Long userId, Course course) {
		// 오늘의 문제를 풀었는지 확인합니다.
		ProblemResponseDto todayProblem = problemService.findTodayProblemByCourse(course);
		return problemSolvingService.isProblemAlreadySolved(userId, todayProblem.getId());
	}
}
