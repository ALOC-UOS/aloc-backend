package com.aloc.aloc.problem.service;



import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
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

	@Value("${app.season}")
	private Integer currentSeason;

	public List<UserProblem> getSolvedUserListByProblemId(Long problemId) {
		problemService.checkProblemExist(problemId);

		// 문제를 푼 사용자 목록을 가져옵니다.
		return userProblemService.getSolvedUserListByProblemId(problemId);
	}

	public List<ProblemSolvedResponseDto> getWeeklyProblem(User user) {
		List<Problem> thisWeekProblems = problemService.getWeeklyProblem(user);

		// 이번주 weekly 문제를 풀이 현황과 함께 리턴합니다.
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
		return userProblemService.isProblemSolvedToday(userId, todayProblem.getId());
	}

	public boolean updateUserAndSaveSolvedProblem(User user, Problem problem) {
		boolean isAlreadySolved = userProblemService.isProblemAlreadySolved(user.getId(), problem.getId());
		if (isAlreadySolved) {
			return false; // 이미 푼 문제라면 false 반환
		}
		boolean isSolved = solvedCheckingService.isProblemSolved(user.getBaekjoonId(), problem);
		UserProblem userProblem = userProblemService.getOrCreateUserProblem(user, problem, isSolved);
		if (isSolved) {
			// 코인을 지급하고 사용자 정보를 저장합니다.
			System.out.println("문제를 풀었어요" + problem.getId() + " " + user.getGithubId());
			coinService.addCoinIfEligible(user, problem);
			userProblem.setIsSolved(true);
		}
		userProblemService.saveUserProblem(userProblem);
		return isSolved;
	}

	public boolean updateTodaySolvedProblem(User user, Problem todayProblem) {
		boolean isAlreadySolved = userProblemService.isProblemAlreadySolved(user.getId(), todayProblem.getId());
		if (isAlreadySolved) {
			return false; // 이미 푼 문제라면 false 반환
		}
		boolean isSolved = solvedCheckingService.isProblemSolved(user.getBaekjoonId(), todayProblem);
		UserProblem userProblem = userProblemService.getOrCreateUserProblem(user, todayProblem, isSolved);
		if (isSolved) {
			// 코인을 지급하고 사용자 정보를 저장합니다.
			System.out.println("오늘의 문제를 풀었어요: " + user.getGithubId());
			coinService.addCoinEligibleForTodayProblem(user, todayProblem);
			userProblem.setIsSolved(true);
		}
		userProblemService.saveUserProblem(userProblem);
		return isSolved;
	}

	public List<Integer> getThisWeekSolvedCount(User user) {
		// 이번 주차 문제를 가져옵니다.
		List<Problem> thisWeekProblems = problemService.getThisWeekProblem(user);
		// 이번 주차 문제 중 푼 문제 수, 문제 수, 안 푼 문제 수를 가져옵니다.
		long solvedCount = thisWeekProblems.stream()
			.filter(problem -> userProblemService.isProblemAlreadySolved(user.getId(), problem.getId()))
			.count();
		int totalProblems = thisWeekProblems.size();
		int unsolvedCount = totalProblems - Math.toIntExact(solvedCount);

		// List에 결과를 담아 반환
		return Arrays.asList(Math.toIntExact(solvedCount), totalProblems, unsolvedCount);
	}

	public void addUserProblemRecord(User user) {
		List<Problem> problems = problemService.getHiddenProblemsBySeasonAndCourse(user.getCourse());
		for (Problem problem : problems) {
			userProblemService.saveUserProblem(UserProblem.builder()
				.user(user)
				.problem(problemService.findProblemById(problem.getId()))
				.season(currentSeason)
				.build());
		}
	}
}
