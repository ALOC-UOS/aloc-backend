package com.aloc.aloc.scraper;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.enums.CourseRoutineTier;
import com.aloc.aloc.algorithm.service.AlgorithmService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WeeklyProblemAdditionStrategy implements ProblemAdditionStrategy {
  private final AlgorithmService algorithmService;
  private final ProblemScrapingService problemScrapingService;

  @Override
  public String addProblems() throws IOException {
    return problemScrapingService.addProblemsForStrategy(this);
  }

  @Override
  public Algorithm getAlgorithm() {
    return algorithmService.findWeeklyAlgorithm();
  }

  @Override
  public CourseRoutineTier[] getRelevantTiers() {
    return new CourseRoutineTier[] {CourseRoutineTier.HALF_WEEKLY, CourseRoutineTier.FULL_WEEKLY};
  }

  @Override
  public void updateAlgorithmHidden(Algorithm algorithm) {}
}
