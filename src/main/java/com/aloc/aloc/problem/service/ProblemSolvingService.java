package com.aloc.aloc.problem.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.entity.SolvedProblem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.problem.repository.SolvedProblemRepository;
import com.aloc.aloc.scraper.SolvedScrapingService;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemSolvingService {
	private final ProblemRepository problemRepository;
	private final SolvedProblemRepository solvedProblemRepository;
	private final SolvedScrapingService solvedScrapingService;
	private final UserRepository userRepository;


	boolean isProblemAlreadySolved(Long userId, Long problemId) {
		return solvedProblemRepository.existsByUserIdAndProblemId(userId, problemId);
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
		long solvedUserCount = solvedProblemRepository.countSolvingUsersByProblemId(problemId);
		return solvedUserCount <= 2 ? 50 : 30;
	}

	private void updateUserAndSaveSolvedProblem(User user, Long problemId) {
		// 코인을 지급하고 사용자 정보를 저장합니다.
		int coinToAdd = calculateCoinToAdd(problemId);
		user.addCoin(coinToAdd);
		userRepository.save(user);

		// 문제를 푼 정보를 저장합니다.
		SolvedProblem solvedProblem = SolvedProblem.builder()
			.user(user)
			.problem(problemRepository.getReferenceById(problemId))
			.build();
		solvedProblemRepository.save(solvedProblem);
	}

	public List<SolvedProblem> getSolvedUserListByProblemId(Long problemId) {
		return solvedProblemRepository.findAllByProblemId(problemId);
	}

	public List<SolvedProblem> getSolvedProblemListByUser(Long userId) {
		return solvedProblemRepository.findAllByUserIdOrderBySolvedAtDesc(userId);
	}

	public int getSolvedCount(Long userId) {
		return solvedProblemRepository.countByUserId(userId);
	}
}
