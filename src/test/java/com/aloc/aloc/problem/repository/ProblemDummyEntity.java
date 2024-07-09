package com.aloc.aloc.problem.repository;

import java.time.LocalDateTime;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problemtype.ProblemType;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;

import jakarta.persistence.EntityManager;


public class ProblemDummyEntity {
	public Problem problem1;
	public Problem problem2;
	public Problem problem3;
	public Problem problem4;
	public ProblemType problemType1;
	public ProblemType problemType2;
	public Algorithm algorithm1;
	public Algorithm algorithm2;

	public void setupEntities(EntityManager em) {
		problemType1 = ProblemType.builder().routine(Routine.DAILY).course(Course.FULL).build();
		problemType2 = ProblemType.builder().routine(Routine.WEEKLY).course(Course.HALF).build();
		em.persist(problemType1);
		em.persist(problemType2);

		algorithm1 = new Algorithm(1, 1, "Algorithm 1", 2, false);
		algorithm2 = new Algorithm(2, 2, "Algorithm 2", 2, true);
		em.persist(algorithm1);
		em.persist(algorithm2);

		problem1 = new Problem("Problem 1", 3, algorithm1, 1, problemType1);
		problem2 = new Problem("Problem 2", 4, algorithm1, 2, problemType1);
		problem3 = new Problem("Problem 3", 5, algorithm2, 3, problemType2);
		problem4 = new Problem("Problem 4", 6, algorithm1, 4, problemType2);

		problem1.setUpdatedAt(LocalDateTime.now().plusDays(1));
		problem2.setUpdatedAt(LocalDateTime.now().plusDays(2));
		problem3.setUpdatedAt(LocalDateTime.now().plusDays(3));
		problem4.setUpdatedAt(LocalDateTime.now().minusDays(1));

		problem1.setHidden(false);
		problem2.setHidden(true);
		problem3.setHidden(false);
		problem4.setHidden(false);

		em.persist(problem1);
		em.persist(problem2);
		em.persist(problem3);
		em.persist(problem4);
		em.flush();
		em.clear();
	}
}
