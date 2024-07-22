package com.aloc.aloc.scraper.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.repository.AlgorithmRepository;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.repository.ProblemRepository;
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
	private AlgorithmRepository algorithmRepository;
	@Mock
	private ProblemRepository problemRepository;
	@Mock
	private ProblemTypeRepository problemTypeRepository;
	@Mock
	private TagRepository tagRepository;
	@Mock
	private UserService userService;
	@Mock
	private ProblemTagRepository problemTagRepository;

	@InjectMocks
	private ProblemScrapingService problemScraperService;
	private List<Algorithm> algorithms;
	private List<ProblemType> problemTypes;

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
	}

	@Test
	@DisplayName("이번주 문제 추가 성공")
	void addProblemForThisWeekSuccess() throws IOException {
		// given
		// 시즌2 시작 주차를 기준 알고리즘 반환하도록 구성
//		when(problemScraperService.findWeeklyAlgorithm()).thenReturn(algorithms.get(5));
		when(algorithmRepository.findFirstBySeasonAndHiddenTrueOrderByCreatedAtAsc(anyInt()))
			.thenReturn(Optional.of(algorithms.get(5)));
		when(algorithmRepository.findFirstBySeasonAndHiddenFalseOrderByCreatedAtDesc(anyInt()))
			.thenReturn(Optional.empty());
		when(algorithmRepository.findFirstBySeasonAndHiddenFalseOrderByCreatedAtDesc(anyInt()))
			.thenReturn(Optional.of(algorithms.get(3)));

		// 모든 ProblemType 구성
		for (ProblemType problemType : problemTypes) {
			when(problemTypeRepository.findByCourseAndRoutine(problemType.getCourse(), problemType.getRoutine()))
				.thenReturn(Optional.of(problemType));
		}

		// 테스트를 위해 중복이 없다는 가정으로 항상 문제와 태그 추가하도록 구성
		when(problemRepository.existsByProblemIdAndProblemType_Course(anyInt(), any(Course.class)))
			.thenReturn(false);
		when(tagRepository.findByKoreanNameAndEnglishName(anyString(), anyString()))
			.thenReturn(Optional.empty());

		// when
		problemScraperService.addProblemsForThisWeek();

		// then
		verify(algorithmRepository).findFirstBySeasonAndHiddenTrueOrderByCreatedAtAsc(anyInt());
		verify(algorithmRepository).findFirstBySeasonAndHiddenFalseOrderByCreatedAtDesc(anyInt());
		verify(algorithmRepository).findFirstBySeasonAndHiddenFalseOrderByCreatedAtDesc(anyInt());

		ArgumentCaptor<Problem> problemCaptor = ArgumentCaptor.forClass(Problem.class);
		verify(problemRepository, atLeastOnce()).save(problemCaptor.capture());

		List<Problem> savedProblems = problemCaptor.getAllValues();
		assertFalse(savedProblems.isEmpty());
		assertEquals(48, savedProblems.size()); // 문제 선 저장 -> 태그 초기화 -> 문제 저장 (24 * 2개)

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
