package com.aloc.aloc.scraper.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.enums.CourseRoutineTier;
import com.aloc.aloc.algorithm.service.AlgorithmService;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.service.ProblemService;
import com.aloc.aloc.problemtag.repository.ProblemTagRepository;
import com.aloc.aloc.problemtype.ProblemType;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;
import com.aloc.aloc.problemtype.repository.ProblemTypeRepository;
import com.aloc.aloc.scraper.ProblemAdditionStrategy;
import com.aloc.aloc.scraper.ProblemScrapingService;
import com.aloc.aloc.tag.repository.TagRepository;
import com.aloc.aloc.user.service.UserService;
import java.io.IOException;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
class ProblemScraperServiceTest {
  @Mock private AlgorithmService algorithmService;
  @Mock private ProblemTypeRepository problemTypeRepository;
  @Mock private ProblemService problemService;
  // 삭제하면 안됨
  @Mock private TagRepository tagRepository;
  @Mock private ProblemTagRepository problemTagRepository;
  @Mock private UserService userService;

  @Mock private ProblemAdditionStrategy mockStrategy;

  @InjectMocks private ProblemScrapingService problemScrapingService;
  private List<Algorithm> algorithms;
  private List<ProblemType> problemTypes;
  private static final int CURRENT_SEASON = 2;

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
      Algorithm algorithm =
          Algorithm.builder()
              .algorithmId(entry.getKey())
              .season(2)
              .name(entry.getValue())
              .hidden(false)
              .build();
      algorithms.add(algorithm);
    }

    for (Map.Entry<Integer, String> entry : season2Algorithm.entrySet()) {
      Algorithm algorithm =
          Algorithm.builder()
              .algorithmId(entry.getKey())
              .season(2)
              .name(entry.getValue())
              .hidden(true)
              .build();
      algorithms.add(algorithm);
    }
    ProblemType halfWeekly =
        ProblemType.builder().course(Course.HALF).routine(Routine.WEEKLY).build();
    ProblemType halfDaily =
        ProblemType.builder().course(Course.HALF).routine(Routine.DAILY).build();
    ProblemType fullWeekly =
        ProblemType.builder().course(Course.FULL).routine(Routine.WEEKLY).build();
    ProblemType fullDaily =
        ProblemType.builder().course(Course.FULL).routine(Routine.DAILY).build();
    problemTypes.add(halfWeekly);
    problemTypes.add(halfDaily);
    problemTypes.add(fullWeekly);
    problemTypes.add(fullDaily);
    ReflectionTestUtils.setField(problemScrapingService, "currentSeason", CURRENT_SEASON);
  }

  @Test
  @DisplayName("이번주 문제 추가 성공")
  void addProblemForThisWeekSuccess() throws ExecutionException, InterruptedException, IOException {
    Algorithm dailyAlgorithm = algorithms.get(0);
    ProblemType problemType = new ProblemType();

    when(problemTypeRepository.findByCourseAndRoutine(any(), any()))
        .thenReturn(Optional.of(problemType));
    when(problemService.isNewProblem(anyInt(), any(), anyInt())).thenReturn(true);

    ProblemScrapingService spyService = spy(problemScrapingService);
    doReturn(
            "https://www.acmicpc.net/problemset?sort=ac_desc&tier=9,10,11,12,13&algo=158&algo_if=and")
        .when(spyService)
        .getProblemUrl(anyList(), anyInt());

    List<Problem> mockProblems = new ArrayList<>();
    for (int i = 1; i <= 7; i++) {
      Problem mockProblem =
          Problem.builder().problemId(i).title("Test Problem " + i).difficulty((i % 5) + 4).build();
      mockProblems.add(mockProblem);
    }

    doReturn(mockProblems).when(spyService).crawlProblems(anyString(), any(), any());

    when(problemService.saveProblem(any(Problem.class))).thenReturn(mockProblems.get(0));

    CourseRoutineTier[] tiers = {CourseRoutineTier.HALF_DAILY, CourseRoutineTier.FULL_DAILY};
    when(mockStrategy.getAlgorithm()).thenReturn(dailyAlgorithm);
    when(mockStrategy.getRelevantTiers()).thenReturn(tiers);

    String result = spyService.addProblemsForStrategy(mockStrategy);
    assertFalse(result.isEmpty(), "Result should not be empty");

    ArgumentCaptor<Problem> problemCaptor = ArgumentCaptor.forClass(Problem.class);

    List<Problem> savedProblems = problemCaptor.getAllValues();

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
