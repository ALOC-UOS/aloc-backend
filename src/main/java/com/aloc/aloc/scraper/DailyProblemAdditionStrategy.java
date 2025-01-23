package com.aloc.aloc.scraper;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.enums.CourseRoutineTier;
import com.aloc.aloc.algorithm.service.AlgorithmService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyProblemAdditionStrategy implements ProblemAdditionStrategy {
  private final AlgorithmService algorithmService;
  private final ProblemScrapingService problemScrapingService;

  @Override
  public String addProblems() throws IOException {
    return problemScrapingService.addProblemsForStrategy(this);
  }

  @Override
  // TODO: 방학되면 다시 findDaily로 수정
  public Algorithm getAlgorithm() {
    return algorithmService.findWeeklyAlgorithm();
  }

  @Override
  public CourseRoutineTier[] getRelevantTiers() {
    return new CourseRoutineTier[] {CourseRoutineTier.HALF_DAILY, CourseRoutineTier.FULL_DAILY};
  }

  @Override
  public void updateAlgorithmHidden(Algorithm algorithm) {
    algorithm.setHiddenFalse();
    algorithmService.saveAlgorithm(algorithm);
  }
}
