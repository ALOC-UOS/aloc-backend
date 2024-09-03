package com.aloc.aloc.scraper.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.service.AlgorithmService;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.service.ProblemService;
import com.aloc.aloc.problemtag.repository.ProblemTagRepository;
import com.aloc.aloc.problemtype.ProblemType;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;
import com.aloc.aloc.problemtype.repository.ProblemTypeRepository;
import com.aloc.aloc.scraper.ProblemScrapingService;
import com.aloc.aloc.tag.repository.TagRepository;
import com.aloc.aloc.user.service.UserService;


@ExtendWith(MockitoExtension.class)
@Transactional
class ProblemScraperServiceTest {
	@Mock
	private AlgorithmService algorithmService;
	@Mock
	private ProblemTypeRepository problemTypeRepository;
	@Mock
	private ProblemService problemService;
	// 삭제하면 안됨
	@Mock
	private TagRepository tagRepository;
	@Mock
	private ProblemTagRepository problemTagRepository;
	@Mock
	private UserService userService;

	@InjectMocks
	private ProblemScrapingService problemScrapingService;
	private List<Algorithm> algorithms;
	private List<ProblemType> problemTypes;
	private static final int currentSeason = 2;

	@BeforeEach
	void init() {
		algorithms = new ArrayList<>();
		problemTypes = new ArrayList<>();
		Map<Integer, String> season1Algorithm = new HashMap<>();
		Map<Integer, String> season2Algorithm = new HashMap<>();

		season1Algorithm.put(123, "수학");
		season1Algorithm.put(102, "구현");
		season1Algorithm.put(24, "다이나믹 프로그래밍(DP)");

		season2Algorithm.put(175, "자료 구조");
		season2Algorithm.put(7, "그래프 이론");
		season2Algorithm.put(33, "그리디 알고리즘");

		for (Map.Entry<Integer, String> entry : season1Algorithm.entrySet()) {
			Algorithm algorithm = Algorithm.builder()
				.algorithmId(entry.getKey())
				.season(2)
				.name(entry.getValue())
				.hidden(false)
				.build();
			algorithms.add(algorithm);
		}

		for (Map.Entry<Integer, String> entry : season2Algorithm.entrySet()) {
			Algorithm algorithm = Algorithm.builder()
				.algorithmId(entry.getKey())
				.season(2)
				.name(entry.getValue())
				.hidden(true)
				.build();
			algorithms.add(algorithm);
		}
		ProblemType halfWeekly = ProblemType.builder()
			.course(Course.HALF)
			.routine(Routine.WEEKLY)
			.build();
		ProblemType halfDaily = ProblemType.builder()
			.course(Course.HALF)
			.routine(Routine.DAILY)
			.build();
		ProblemType fullWeekly = ProblemType.builder()
			.course(Course.FULL)
			.routine(Routine.WEEKLY)
			.build();
		ProblemType fullDaily = ProblemType.builder()
			.course(Course.FULL)
			.routine(Routine.DAILY)
			.build();
		problemTypes.add(halfWeekly);
		problemTypes.add(halfDaily);
		problemTypes.add(fullWeekly);
		problemTypes.add(fullDaily);
		ReflectionTestUtils.setField(problemScrapingService, "currentSeason", currentSeason);
	}

	@Test
	@DisplayName("이번주 문제 추가 성공")
	void addProblemForThisWeekSuccess() throws ExecutionException, InterruptedException {
		// given
		// 시즌2 시작 주차를 기준 알고리즘 반환하도록 구성
		Algorithm weeklyAlgorithm = algorithms.get(5);
		Algorithm dailyAlgorithm = algorithms.get(0);
		ProblemType problemType = new ProblemType();

		Problem savedProblem = Problem.builder()
			.problemId(123)
			.title("문제")
			.algorithm(weeklyAlgorithm)
			.problemType(problemType)
			.difficulty(3)
			.build();

		savedProblem.setId(1L);

//		when(algorithmService.findWeeklyAlgorithm())
//			.thenReturn(weeklyAlgorithm);
		when(algorithmService.findDailyAlgorithm())
			.thenReturn(dailyAlgorithm);
		when(problemTypeRepository.findByCourseAndRoutine(any(), any()))
			.thenReturn(Optional.of(problemType));
		when(problemService.isNewProblem(anyInt(), any(), anyInt()))
			.thenReturn(true);
		when(problemService.saveProblem(any(Problem.class))
		).thenReturn(savedProblem);

		// when
		problemScrapingService.addProblemsForThisWeek();

		// then
		verify(problemService, times(14)).saveProblem(any(Problem.class)); // 4 types * 6 problems each
//		verify(algorithmService, times(1)).saveAlgorithm(weeklyAlgorithm);

		ArgumentCaptor<Problem> problemCaptor = ArgumentCaptor.forClass(Problem.class);

		List<Problem> savedProblems = problemCaptor.getAllValues();

		// 각 문제가 잘 저장되었는지 확인
		for (Problem problem : savedProblems) {
			assertNotNull(problem.getProblemId());
			assertNotNull(problem.getTitle());
			assertNotNull(problem.getAlgorithm());
			assertNotNull(problem.getProblemType());
			assertNotNull(problem.getDifficulty());
			assertNotNull(problem.getHidden());
			assertNotNull(problem.getProblemTagList());
		}
	}
}
