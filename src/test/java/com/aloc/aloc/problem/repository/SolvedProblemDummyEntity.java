package com.aloc.aloc.problem.repository;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserProblem;
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
	UserProblem up1;
	UserProblem up2;
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
			new Algorithm(1, 1, "Algorithm 1", 2, null);
		em.persist(algorithm1);

		problemType1 =
			ProblemType.builder().routine(Routine.DAILY).course(Course.FULL).build();
		em.persist(problemType1);

		problem1 =
			new Problem("Problem 1", 3, algorithm1, 1, problemType1);
		em.persist(problem1);

		up1 = UserProblem.builder().user(user1).problem(problem1).season(2).build();
		up2 = UserProblem.builder().user(user2).problem(problem1).season(2).build();
		em.persist(up1);
		em.persist(up2);
	}

}
