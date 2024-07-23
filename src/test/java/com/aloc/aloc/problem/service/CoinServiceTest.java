package com.aloc.aloc.problem.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.service.AlgorithmService;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.problem.repository.UserProblemRepository;
import com.aloc.aloc.problemtype.enums.Course;

@ExtendWith(MockitoExtension.class)
class CoinServiceTest {
	@Mock
	private UserProblemRepository userProblemRepository;
	@Mock
	private AlgorithmService algorithmService;
	@Mock
	private ProblemRepository problemRepository;
	@InjectMocks
	private CoinService coinService;

	private final Integer currentSeason = 2;
	private static final int COINS_FOR_1ST_PLACE = 50;
	private static final int COINS_FOR_2ND_PLACE = 40;
	private static final int COINS_FOR_3RD_PLACE = 30;
	private static final int COINS_FOR_OTHERS = 20;
	private static final int COINS_FOR_FULL = 200;
	private static final int COINS_FOR_HALF = 100;
	private final Long problemId = 1L;
	private Algorithm algorithmOfThisWeek;
	private Algorithm algorithmOfProblem;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(coinService, "currentSeason", currentSeason);
		algorithmOfProblem = new Algorithm(1, 1, "알고리즘 1", currentSeason, false);
		algorithmOfThisWeek = new Algorithm(2, 2, "알고리즘 2", currentSeason, false);
	}

	@Test
	@DisplayName("daily 문제 1등으로 풀었을 때 테스트")
	void calculateCoinToAddForDaily_FirstPlace() {
		when(userProblemRepository.countSolvingUsersByProblemId(problemId, currentSeason)).thenReturn(0);

		int coins = coinService.calculateCoinToAddForDaily(problemId);

		assertEquals(COINS_FOR_1ST_PLACE, coins);
	}

	@Test
	@DisplayName("daily 문제 2등으로 풀었을 때 테스트")
	void calculateCoinToAddForDaily_SecondPlace() {
		when(userProblemRepository.countSolvingUsersByProblemId(problemId, currentSeason)).thenReturn(1);

		int coins = coinService.calculateCoinToAddForDaily(problemId);

		assertEquals(COINS_FOR_2ND_PLACE, coins);
	}

	@Test
	@DisplayName("daily 문제 3등으로 풀었을 때 테스트")
	void calculateCoinToAddForDaily_ThirdPlace() {
		when(userProblemRepository.countSolvingUsersByProblemId(problemId, currentSeason)).thenReturn(2);

		int coins = coinService.calculateCoinToAddForDaily(problemId);

		assertEquals(COINS_FOR_3RD_PLACE, coins);
	}

	@Test
	@DisplayName("daily 문제 4등 이하로 풀었을 때 테스트")
	void calculateCoinToAddForDaily_Others() {
		when(userProblemRepository.countSolvingUsersByProblemId(problemId, currentSeason)).thenReturn(3);

		int coins = coinService.calculateCoinToAddForDaily(problemId);

		assertEquals(COINS_FOR_OTHERS, coins);
	}

	@Test
	@DisplayName("이번주에 FULL 코스 유저가 이번주 weekly 문제 다 풀었을 때 테스트")
	void calculateCoinToAddForWeekly_FullCourse() {
		when(algorithmService.getAlgorithmBySeason(currentSeason)).thenReturn(Optional.of(algorithmOfProblem));
		when(problemRepository.findAllByAlgorithm(algorithmOfProblem)).thenReturn(List.of(new Problem()));
		when(userProblemRepository.countByProblemsIn(any(List.class))).thenReturn(0);

		int coins = coinService.calculateCoinToAddForWeekly(algorithmOfProblem, Course.FULL);

		assertEquals(COINS_FOR_FULL, coins);
	}

	@Test
	@DisplayName("이번주에 HALF 코스 유저가 이번주 weekly 문제 다 풀었을 때 테스트")
	void calculateCoinToAddForWeekly_HalfCourse() {
		when(algorithmService.getAlgorithmBySeason(currentSeason)).thenReturn(Optional.of(algorithmOfProblem));
		when(problemRepository.findAllByAlgorithm(algorithmOfProblem)).thenReturn(List.of(new Problem()));
		when(userProblemRepository.countByProblemsIn(any(List.class))).thenReturn(0);

		int coins = coinService.calculateCoinToAddForWeekly(algorithmOfProblem, Course.HALF);

		assertEquals(COINS_FOR_HALF, coins);
	}

	@Test
	@DisplayName("해당 weekly 문제의 알고리즘이 이번주 알고리즘이 아닐 때 테스트")
	void calculateCoinToAddForWeekly_NoMatchingAlgorithm() {
		when(algorithmService.getAlgorithmBySeason(currentSeason)).thenReturn(Optional.of(algorithmOfThisWeek));

		int coins = coinService.calculateCoinToAddForWeekly(algorithmOfProblem, Course.FULL);

		assertEquals(0, coins);
	}

	@Test
	@DisplayName("이번주 weekly 문제를 다 풀지 않았을 때")
	void calculateCoinToAddForWeekly_SomeUnsolvedProblems() {
		when(algorithmService.getAlgorithmBySeason(currentSeason)).thenReturn(Optional.of(algorithmOfProblem));
		when(problemRepository.findAllByAlgorithm(algorithmOfProblem)).thenReturn(List.of(new Problem()));
		when(userProblemRepository.countByProblemsIn(any(List.class))).thenReturn(1);

		int coins = coinService.calculateCoinToAddForWeekly(algorithmOfProblem, Course.FULL);

		assertEquals(0, coins);
	}
}
