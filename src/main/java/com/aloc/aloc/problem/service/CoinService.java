package com.aloc.aloc.problem.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.service.AlgorithmService;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.problem.repository.UserProblemRepository;
import com.aloc.aloc.problemtype.enums.Course;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoinService {
	private final AlgorithmService algorithmService;
	private final UserProblemRepository userProblemRepository;
	private final ProblemRepository problemRepository;

	@Value("${app.season}")
	private Integer currentSeason;
	private static final int COINS_FOR_1ST_PLACE = 50;
	private static final int COINS_FOR_2ND_PLACE = 40;
	private static final int COINS_FOR_3RD_PLACE = 30;
	private static final int COINS_FOR_4TH_PLACE = 20;
	private static final int COINS_FOR_OTHERS = 10;
	private static final int COINS_FOR_FULL = 200;
	private static final int COINS_FOR_HALF = 200;

	public int calculateCoinToAddForDaily(Long problemId) {
		// daily문제 푼 순서 1등 50, 2등 40, 3등 30, 4등 20, 5등 이하 10 코인을 지급합니다.
		int solvedUserCount = userProblemRepository.countSolvingUsersByProblemId(problemId, currentSeason);
		return getCoinsForPlace(solvedUserCount);
	}

	public int calculateCoinToAddForWeekly(Algorithm algorithm, Course course) {
		Algorithm thisWeekAlgorithm = algorithmService.getAlgorithmBySeason(currentSeason)
			.orElseThrow(() -> new RuntimeException("이번주 알고리즘이 존재하지 않습니다."));
		if (thisWeekAlgorithm.equals(algorithm)) {
			if (getUnsolvedProblemCount(problemRepository.findAllByAlgorithm(algorithm)) == 0) {
				return getCoinsForCourse(course);
			}
		}
		return 0;
	}

	private int getUnsolvedProblemCount(List<Problem> thisWeekProblems) {
		return userProblemRepository.countByProblemsIn(thisWeekProblems);
	}

	private int getCoinsForPlace(int solvedUserCount) {
		return switch (solvedUserCount) {
			case 0 -> COINS_FOR_1ST_PLACE;
			case 1 -> COINS_FOR_2ND_PLACE;
			case 2 -> COINS_FOR_3RD_PLACE;
			case 3 -> COINS_FOR_4TH_PLACE;
			default -> COINS_FOR_OTHERS;
		};
	}

	private int getCoinsForCourse(Course course) {
		return switch (course) {
			case FULL -> COINS_FOR_FULL;
			case HALF -> COINS_FOR_HALF;
		};
	}
}
