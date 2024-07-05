package com.aloc.aloc.problem.repository;

import com.aloc.aloc.algorithm.Algorithm;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.SolvedProblem;
import com.aloc.aloc.problemtype.ProblemType;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;
import com.aloc.aloc.user.User;

import jakarta.persistence.EntityManager;


public class SolvedProblemDummyEntity {
	User user1;
	User user2;
	Problem problem1;
	Algorithm algorithm1;
	ProblemType problemType1;
	SolvedProblem sp1;
	SolvedProblem sp2;
	public void setupEntities(EntityManager em) {
		user1 = User.builder()
			.githubId("githubId1")
			.baekjoonId("baekjoonId")
			.username("username1")
			.password("password")
			.studentId("studentId")
			.build();
		user2 = User.builder()
			.githubId("githubId2")
			.baekjoonId("baekjoonId2")
			.username("username2")
			.password("password")
			.studentId("studentId2")
			.build();
		em.persist(user1);
		em.persist(user2);

		algorithm1 =
			new Algorithm(1, "Algorithm 1", 2, null);
		em.persist(algorithm1);

		problemType1 =
			ProblemType.builder().routine(Routine.DAILY).course(Course.FULL).build();
		em.persist(problemType1);

		problem1 =
			new Problem("Problem 1", 3, algorithm1, 1, problemType1);
		em.persist(problem1);

		sp1 = new SolvedProblem(user1, problem1);
		sp2 = new SolvedProblem(user2, problem1);
		em.persist(sp1);
		em.persist(sp2);
	}

}
