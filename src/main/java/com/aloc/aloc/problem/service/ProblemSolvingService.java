package com.aloc.aloc.problem.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserProblem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.problem.repository.UserProblemRepository;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;
import com.aloc.aloc.scraper.SolvedScrapingService;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.repository.UserRepository;
import com.aloc.aloc.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemSolvingService {
	private final ProblemRepository problemRepository;
	private final ProblemService problemService;
	private final UserProblemRepository userProblemRepository;
	private final SolvedScrapingService solvedScrapingService;
	private final UserRepository userRepository;
	private final UserService userService;
	private final CoinService coinService;

	@Value("${app.season}")
	private Integer currentSeason;

	boolean isProblemAlreadySolved(Long userId, Long problemId) {
		// 사용자가 이미 푼 문제인지 확인합니다.
		return userProblemRepository.existsByUserIdAndProblemIdAndIsSolvedIsTrue(userId, problemId);
	}

	void updateUserAndSaveSolvedProblem(User user, Problem problem) {
		boolean isSolved = solvedScrapingService.isProblemSolved(user.getBaekjoonId(), problem);

		if (isSolved) {
			// 코인을 지급하고 사용자 정보를 저장합니다.
			addCoinIfEligible(user, problem);
			user.addSolvedCount();
			userService.checkUserRank(user);
		}

		// 해결 정보가 있으면 업데이트하고 없으면 새로 생성합니다.
		UserProblem userProblem = getOrCreateUserProblem(user, problem, isSolved);
		saveUserAndUserProblem(user, userProblem);
	}

	private void addCoinIfEligible(User user, Problem problem) {
		if (isEligibleForCoin(user, problem)) {
			int coinToAdd = calculateCoinToAdd(problem, user.getCourse());
			user.addCoin(coinToAdd);
		}
	}

	private boolean isEligibleForCoin(User user, Problem problem) {
		if (problem.getProblemType().getRoutine() == Routine.WEEKLY) {
			return true; // Weekly 문제는 항상 코인을 받음
		} else if (problem.getProblemType().getRoutine() == Routine.DAILY) {
			ProblemResponseDto todayProblem = problemService.findTodayProblemByCourse(user.getCourse());
			return problem.getId().equals(todayProblem.getId()); // 오늘의 문제인 경우에만 코인을 받음
		}
		return false; // 다른 경우에는 코인을 받지 않음
	}

	private int calculateCoinToAdd(Problem problem, Course course) {
		return problem.getProblemType().getRoutine().equals(Routine.DAILY)
			? coinService.calculateCoinToAddForDaily(problem.getId())
			: coinService.calculateCoinToAddForWeekly(problem.getAlgorithm(), course);
	}

	public UserProblem getOrCreateUserProblem(User user, Problem problem, boolean isSolved) {
		// 해결 정보가 있으면 업데이트하고 없으면 새로 생성합니다.
		System.out.println("user.getId() : " + user.getId() + "problem.getId() : " + problem.getId());
		return userProblemRepository.findByUserIdAndProblemId(
			user.getId(), problem.getId())
			.orElse(
				UserProblem.builder()
					.user(user)
					.problem(problemRepository.getReferenceById(problem.getId()))
					.isSolved(isSolved)
					.season(currentSeason)
					.build()
			);
	}

	private void saveUserAndUserProblem(User user, UserProblem userProblem) {
		userProblem.setIsSolved(true);
		userProblemRepository.save(userProblem);
		userRepository.save(user);
	}

	public List<UserProblem> getSolvedUserListByProblemId(Long problemId) {
		return userProblemRepository.findAllByProblemIdAndIsSolvedIsTrue(problemId);
	}


	// 시즌, 풀이 여부, 루틴에 따라 유저의 문제 목록을 가져옵니다.
	public List<UserProblem> getUserProblemList(Long userId, Integer season, Boolean isSolved, Routine routine) {
		return userProblemRepository.findAllByUserIdAndSeasonAndIsSolvedOrderBySolvedAtDesc(
			userId, season, isSolved, routine);
	}

	// TODO: user.solvedCount로 변경
	// 이번 시즌 동안 해결한 Daily 문제 수를 가져옵니다.
	public int getSolvedCountByUserId(Long userId) {
		return getUserProblemList(userId, currentSeason, true, Routine.DAILY).size();
	}
}
