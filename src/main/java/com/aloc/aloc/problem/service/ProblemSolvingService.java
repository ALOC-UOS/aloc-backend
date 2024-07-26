package com.aloc.aloc.problem.service;



import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserProblem;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;
import com.aloc.aloc.scraper.SolvedCheckingService;
import com.aloc.aloc.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemSolvingService {
	private final ProblemService problemService;
	private final UserProblemService userProblemService;
	private final ProblemMapper problemMapper;
	private final SolvedCheckingService solvedCheckingService;
	private final CoinService coinService;

	public List<UserProblem> getSolvedUserListByProblemId(Long problemId) {
		problemService.checkProblemExist(problemId);

		// 문제를 푼 사용자 목록을 가져옵니다.
		return userProblemService.getSolvedUserListByProblemId(problemId);
	}

	public List<ProblemSolvedResponseDto> getWeeklyCompletionStatus(User user) {
		List<Problem> thisWeekProblems = problemService.getThisWeekProblems(user);

		// 문제 풀이 현황을 리턴합니다.
		return thisWeekProblems.stream()
			.map(problem -> {
				boolean isSolved = userProblemService.isProblemAlreadySolved(user.getId(), problem.getId());
				return problemMapper.mapToProblemSolvedResponseDto(problem, isSolved);
			})
			.collect(Collectors.toList());
	}

	public List<ProblemSolvedResponseDto> getUnsolvedProblemListByUser(User user, Integer season, Routine routine) {
		List<UserProblem> unsolvedProblems
			= userProblemService.getUserProblemList(user.getId(), season, false, routine);

		return problemMapper.mapSolvedProblemToDtoList(unsolvedProblems);
	}

	public List<ProblemSolvedResponseDto> getSolvedProblemListByUser(User user, Integer season, Routine routine) {
		List<UserProblem> solvedProblems
			= userProblemService.getUserProblemList(user.getId(), season, true, routine);
		return problemMapper.mapSolvedProblemToDtoList(solvedProblems);
	}

	public Boolean getTodayProblemSolved(Long userId, Course course) {
		Problem todayProblem = problemService.findTodayProblemByCourse(course);
		return userProblemService.isProblemAlreadySolved(userId, todayProblem.getId());
	}

	public boolean updateUserAndSaveSolvedProblem(User user, Problem problem, Long todayProblemId) {
		boolean isSolved = solvedCheckingService.isProblemSolved(user.getBaekjoonId(), problem);

		if (isSolved) {
			// 코인을 지급하고 사용자 정보를 저장합니다.
			coinService.addCoinIfEligible(user, problem, todayProblemId);
		}

		// 해결 정보가 있으면 업데이트하고 없으면 새로 생성합니다.
		UserProblem userProblem = userProblemService.getOrCreateUserProblem(user, problem, isSolved);
		saveUserProblem(userProblem);
		return isSolved;
	}

	private void saveUserProblem(UserProblem userProblem) {
		userProblem.setIsSolved(true);
		userProblemService.saveUserProblem(userProblem);
	}

	public List<Integer> getThisWeekSolvedCount(User user) {
		// 이번 주차 문제를 가져옵니다.
		List<Problem> thisWeekProblems = problemService.getThisWeekProblems(user);
		// 이번 주차 문제 중 푼 문제 수, 문제 수, 안 푼 문제 수를 가져옵니다.
		long solvedCount = thisWeekProblems.stream()
			.filter(problem -> userProblemService.isProblemAlreadySolved(user.getId(), problem.getId()))
			.count();
		int totalProblems = thisWeekProblems.size();
		int unsolvedCount = totalProblems - Math.toIntExact(solvedCount);

		// List에 결과를 담아 반환
		return Arrays.asList(Math.toIntExact(solvedCount), totalProblems, unsolvedCount);
	}
}
