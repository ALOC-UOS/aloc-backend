package com.aloc.aloc.problem.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import com.aloc.aloc.problem.dto.response.TodayProblemSolvedResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserProblem;
import com.aloc.aloc.problemtype.enums.Course;
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

	public List<ProblemSolvedResponseDto> getUnsolvedProblemListByUser(User user, Integer season) {
		List<UserProblem> unsolvedProblems
			= userProblemService.getUserProblemList(user.getId(), season, false);

		return problemMapper.mapSolvedProblemToDtoList(unsolvedProblems);
	}

	public List<ProblemSolvedResponseDto> getSolvedProblemListByUser(User user, Integer season) {
		List<UserProblem> solvedProblems
			= userProblemService.getUserProblemList(user.getId(), season, true);
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
		userProblemService.saveUserProblem(userProblem);

		if (isSolved) {
			// 코인을 지급하고 사용자 정보를 저장합니다.
			System.out.println("문제를 풀었어요" + problem.getId() + " " + user.getGithubId());
			userProblem.setIsSolved(true);
			userProblemService.saveUserProblem(userProblem);
			coinService.addCoinIfEligible(user, problem);
		}
		return isSolved;
	}

	public TodayProblemSolvedResponseDto updateTodaySolvedProblem(User user, Problem todayProblem) {
		TodayProblemSolvedResponseDto response = TodayProblemSolvedResponseDto.builder()
			.isSolved(false)
			.build();

		if (userProblemService.isProblemAlreadySolved(user.getId(), todayProblem.getId())) {
			return response;
		}

		boolean isSolved = solvedCheckingService.isProblemSolved(user.getBaekjoonId(), todayProblem);
		UserProblem userProblem = userProblemService.getOrCreateUserProblem(user, todayProblem, isSolved);

		if (isSolved) {
			processSolvedProblem(user, todayProblem, userProblem, response);
		}
		userProblemService.saveUserProblem(userProblem);
		return response;
	}

	private void processSolvedProblem(User user, Problem todayProblem, UserProblem userProblem,
		TodayProblemSolvedResponseDto response) {
		System.out.println("오늘의 문제를 풀었어요: " + user.getGithubId());
		int place = userProblemService.getSolvedUserCount(todayProblem.getId());
		int coin = coinService.addCoinEligibleForTodayProblem(user, todayProblem);

		userProblem.setIsSolved(true);
		updateResponse(response, place, coin, user.getCoin());
	}

	private void updateResponse(TodayProblemSolvedResponseDto response, int place, int coin, int userCoin) {
		response.setPlace(place);
		response.setObtainCoin(coin);
		response.setUserCoin(userCoin);
	}


	public void addUserProblemRecord(User user) {
		List<Problem> problems = problemService.getHiddenProblemsBySeasonAndCourse(user.getCourse());
		for (Problem problem : problems) {
			UserProblem userProblem = userProblemService.getOrCreateUserProblem(
				user,
				problem,
				false
			);
			userProblemService.saveUserProblem(userProblem);
		}
	}
}

