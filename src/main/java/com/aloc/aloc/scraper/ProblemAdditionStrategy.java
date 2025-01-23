package com.aloc.aloc.scraper;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.enums.CourseRoutineTier;
import java.io.IOException;

public interface ProblemAdditionStrategy {
  String addProblems() throws IOException;

  Algorithm getAlgorithm();

  CourseRoutineTier[] getRelevantTiers();

  void updateAlgorithmHidden(Algorithm algorithm);
}
