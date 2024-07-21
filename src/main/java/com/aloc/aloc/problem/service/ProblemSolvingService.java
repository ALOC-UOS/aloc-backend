package com.aloc.aloc.problem.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserProblem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.problem.repository.UserProblemRepository;
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
	private final UserProblemRepository userProblemRepository;
	private final SolvedScrapingService solvedScrapingService;
	private final UserRepository userRepository;
	private final UserService userService;
	private final CoinService coinService;

	@Value("${app.season}")
	private Integer currentSeason;

	// TODO: 변수명 & 로직 확인하기

	boolean isProblemAlreadySolved(Long userId, Long problemId) {
		// 사용자가 이미 푼 문제인지 확인합니다.
		return userProblemRepository.existsByUserIdAndProblemIdAndIsSolvedIsTrue(userId, problemId);
	}

	public String checkAndUpdateProblemSolved(ProblemResponseDto problemDto, User user)
		throws IOException {
		try {
			// 이미 푼 문제인지 확인합니다.
			if (isProblemAlreadySolved(user.getId(), problemDto.getId())) {
				return "alreadySolved";
			}

			// 푼 문제가 아니라면 백준에서 푼 문제인지 확인합니다.
			if (solvedScrapingService.isProblemSolvedToday(user.getBaekjoonId(), problemDto.getId())) {
				Problem problem = problemRepository.findById(problemDto.getId())
						.orElseThrow(() -> new RuntimeException("해당 문제가 존재하지 않습니다."));
				updateUserAndSaveSolvedProblem(user, problem);
				return "success";
			}
			// 풀지 않은 문제더라도 에러를 반환하지 않습니다.
			return "notSolved";
		} catch (IOException e) {
			throw new IOException("백준에서 정보를 불러오던 중 오류가 발생했습니다.");
		}
	}

	public List<UserProblem> getSolvedUserListByProblemId(Long problemId) {
		return userProblemRepository.findAllByProblemIdAndIsSolvedIsTrue(problemId);
	}

	private void updateUserAndSaveSolvedProblem(User user, Problem problem) {
		// 코인을 지급하고 사용자 정보를 저장합니다.
		int coinToAdd;
		if (problem.getProblemType().getRoutine().equals(Routine.DAILY)) {
			coinToAdd = coinService.calculateCoinToAddForDaily(problem.getId());
		} else  {
			coinToAdd = coinService.calculateCoinToAddForWeekly(problem.getAlgorithm(), user.getCourse());
		}
		user.addCoin(coinToAdd);
		userRepository.save(user);
		userService.checkUserRank(user);

		// 해결 정보가 있으면 업데이트하고 없으면 새로 생성합니다.
		UserProblem userProblem = userProblemRepository.findByUserIdAndProblemId(user.getId(), problem.getId())
			.orElse(
				UserProblem.builder()
					.user(user)
					.problem(problemRepository.getReferenceById(problem.getId()))
					.isSolved(true)
					.season(currentSeason)
					.build()
			);
		userProblem.setIsSolved(true);
		userProblemRepository.save(userProblem);
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

	public void addUserProblem(User user, Problem problem)
		throws IOException {
		UserProblem userProblem = UserProblem.builder()
			.user(user)
			.problem(problemRepository.getReferenceById(problem.getId()))
			.isSolved(false)
			.season(currentSeason)
			.build();

		if (solvedScrapingService.isProblemSolved(user.getBaekjoonId(), problem)) {
			userProblem.setIsSolved(true);
		}
		userProblemRepository.save(userProblem);
	}
}
