package com.aloc.aloc.scraper;

import java.io.IOException;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.enums.CourseRoutineTier;
public interface ProblemAdditionStrategy {
	String addProblems() throws IOException;

	Algorithm getAlgorithm();

	CourseRoutineTier[] getRelevantTiers();

	void updateAlgorithmHidden(Algorithm algorithm);
}
