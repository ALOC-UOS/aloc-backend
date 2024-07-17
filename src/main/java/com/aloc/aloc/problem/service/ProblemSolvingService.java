package com.aloc.aloc.problem.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.entity.UserProblem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.problem.repository.UserProblemRepository;
import com.aloc.aloc.scraper.SolvedScrapingService;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.repository.UserRepository;
import com.aloc.aloc.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemSolvingService {
	private final ProblemRepository problemRepository;
	private final UserProblemRepository userProblemRepository;
	private final SolvedScrapingService solvedScrapingService;
	private final UserRepository userRepository;
	private final UserService userService;

	@Value("${app.season}")
	private Integer currentSeason;
	private static final int MAX_SOLVED_USER_COUNT_FOR_BONUS = 2;
	private static final int COINS_FOR_TOP_SOLVERS = 50;
	private static final int COINS_FOR_OTHER_SOLVERS = 30;

	// TODO: 변수명 & 로직 확인하기

	boolean isProblemAlreadySolved(Long userId, Long problemId) {
		// 사용자가 이미 푼 문제인지 확인합니다.
		return userProblemRepository.existsByUserIdAndProblemIdAndIsSolvedIsTrue(userId, problemId);
	}

	public String checkAndUpdateProblemSolved(ProblemResponseDto problem, User user)
		throws IOException {
		try {
			// 이미 푼 문제인지 확인합니다.
			if (isProblemAlreadySolved(user.getId(), problem.getId())) {
				return "alreadySolved";
			}

			// 푼 문제가 아니라면 백준에서 푼 문제인지 확인합니다.
			if (solvedScrapingService.isProblemSolvedToday(user.getBaekjoonId(), problem.getId())) {
				updateUserAndSaveSolvedProblem(user, problem.getId());
				return "success";
			}
			// 풀지 않은 문제더라도 에러를 반환하지 않습니다.
			return "notSolved";
		} catch (IOException e) {
			throw new IOException("백준에서 정보를 불러오던 중 오류가 발생했습니다.");
		}
	}

	private int calculateCoinToAdd(Long problemId) {
		// 2등까지는 50코인, 3등부터는 30코인을 지급합니다.
		long solvedUserCount = userProblemRepository.countSolvingUsersByProblemId(problemId, currentSeason);
		return solvedUserCount <= MAX_SOLVED_USER_COUNT_FOR_BONUS ? COINS_FOR_TOP_SOLVERS : COINS_FOR_OTHER_SOLVERS;
	}

	private void updateUserAndSaveSolvedProblem(User user, Long problemId) {
		// 코인을 지급하고 사용자 정보를 저장합니다.
		int coinToAdd = calculateCoinToAdd(problemId);
		user.addCoin(coinToAdd);
		userRepository.save(user);
		userService.checkUserRank(user);

		// 해결 정보가 있으면 업데이트하고 없으면 새로 생성합니다.
		UserProblem userProblem = userProblemRepository.findByUserIdAndProblemId(user.getId(), problemId)
			.orElse(
				UserProblem.builder()
					.user(user)
					.problem(problemRepository.getReferenceById(problemId))
					.season(currentSeason)
					.build()
			);
		userProblem.setIsSolved(true);
		userProblemRepository.save(userProblem);
	}

	public List<UserProblem> getSolvedUserListByProblemId(Long problemId) {
		return userProblemRepository.findAllByProblemIdAndIsSolvedIsTrue(problemId);
	}

	public List<UserProblem> getSolvedProblemListByUser(Long userId) {
		// 모든 시즌 동안 유저가 푼 문제 목록을 가져옵니다.
		return userProblemRepository.findAllByUserIdAndSeasonAndIsSolvedOrderBySolvedAtDesc(userId, null, true);
	}

	public List<UserProblem> getSolvedProblemListByUserAndSeason(Long userId, Integer season) {
		// 특정 시즌 동안 유저가 푼 문제 목록을 가져옵니다.
		return userProblemRepository.findAllByUserIdAndSeasonAndIsSolvedOrderBySolvedAtDesc(userId, season, true);
	}

	public List<UserProblem> getUnsolvedProblemListByUserAndSeason(Long userId, Integer season) {
		// 특정 시즌 동안 유저가 풀지 않은 문제 목록을 가져옵니다.
		return userProblemRepository.findAllByUserIdAndSeasonAndIsSolvedOrderBySolvedAtDesc(userId, season, false);
	}

	public List<UserProblem> getUnsolvedProblemListByUser(Long userId) {
		// 모든 시즌 동안 유저가 풀지 않은 문제 목록을 가져옵니다.
		return userProblemRepository.findAllByUserIdAndSeasonAndIsSolvedOrderBySolvedAtDesc(userId, null, false);
	}

	public int getSolvedCount(Long userId) {
		return getSolvedProblemListByUserAndSeason(userId, currentSeason).size();
	}
}
