package com.aloc.aloc.problem.service;

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


	public String checkSolved(String username) {
		User user = problemService.findUser(username);
		// 오늘의 문제를 가져옵니다.

		// 오늘의 문제와 다른 문제들의 풀이 여부를 한번에 확인합니다.
		problemService.loadUserProblemRecord(user);

		// 에러가 나지 않는다면 success를 리턴합니다.
		return "success";
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
		List<UserProblem> unsolvedProblems
			= problemSolvingService.getUserProblemList(user.getId(), season, false, routine);

		return problemMapper.mapSolvedProblemToDtoList(unsolvedProblems);
	}

	public List<ProblemSolvedResponseDto> getSolvedProblemListByUser(String githubId, Integer season, Routine routine) {
		User user = problemService.findUser(githubId);

		// 현재 시즌 동안 유저가 푼 문제 목록을 가져옵니다.
		List<UserProblem> solvedProblems
			= problemSolvingService.getUserProblemList(user.getId(), season, true, routine);
		return problemMapper.mapSolvedProblemToDtoList(solvedProblems);
	}

	public List<Integer> getThisWeekSolvedCount(User user) {
		// 이번 주차 문제를 가져옵니다.
		List<Problem> thisWeekProblems = getThisWeekProblems(user);
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

	// TODO: 수정
	public List<Integer> getTotalProblemCount(Course course) {
		// 해당 코스의 공개 된 문제 중 Weekly와 Daily 문제의 개수를 가져옵니다.
		return problemService.getTotalProblemCount(course);
//		Integer weeklyCount = problemRepository.countAllByCourseAndRoutine(currentSeason, course, Routine.WEEKLY);
//		Integer dailyCount = problemRepository.countAllByCourseAndRoutine(currentSeason, course, Routine.DAILY);
//		return List.of(weeklyCount, dailyCount);
	}
}
