package com.aloc.aloc.problem.service;

import java.util.List;
import java.util.Map;

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
	private static final Map<Long, Integer> COIN_REWARDS_DAILY = Map.of(
		0L, COINS_FOR_1ST_PLACE,
		1L, COINS_FOR_2ND_PLACE,
		2L, COINS_FOR_3RD_PLACE,
		3L, COINS_FOR_4TH_PLACE
	);
	private static final Map<Course, Integer> COIN_REWARDS_WEEKLY = Map.of(
		Course.FULL, COINS_FOR_FULL,
		Course.HALF, COINS_FOR_HALF
	);

	public int calculateCoinToAddForDaily(Long problemId) {
		// daily문제 푼 순서 1등 50, 2등 40, 3등 30, 4등 20, 5등 이하 10 코인을 지급합니다.
		long solvedUserCount = userProblemRepository.countSolvingUsersByProblemId(problemId, currentSeason);
		return getCoinsForPlace(solvedUserCount);
	}

	public int calculateCoinToAddForWeekly(Algorithm algorithm, Course course) {
		Algorithm thisWeekAlgorithm = algorithmService.getAlgorithmBySeason(currentSeason)
			.orElseThrow(() -> new RuntimeException("이번주 알고리즘이 존재하지 않습니다."));
		if (thisWeekAlgorithm.equals(algorithm)) {
			long unsolvedProblemCount = getUnsolvedProblemCount(problemRepository.findAllByAlgorithm(algorithm));
			if (unsolvedProblemCount == 0) {
				return getCoinsForCourse(course);
			}
		}
		return 0;
	}

	private long getUnsolvedProblemCount(List<Problem> thisWeekProblems) {
		return userProblemRepository.countByProblemsIn(thisWeekProblems);
	}

	private int getCoinsForPlace(long solvedUserCount) {
		return COIN_REWARDS_DAILY.getOrDefault(solvedUserCount, COINS_FOR_OTHERS);
	}

	private int getCoinsForCourse(Course course) {
		return COIN_REWARDS_WEEKLY.getOrDefault(course, 0);
	}
}
