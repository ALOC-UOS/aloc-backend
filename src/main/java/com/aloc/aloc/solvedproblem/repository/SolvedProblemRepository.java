package com.aloc.aloc.solvedproblem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aloc.aloc.solvedproblem.SolvedProblem;

@Repository
public interface SolvedProblemRepository extends JpaRepository<SolvedProblem, Long> {
	@Query("SELECT sp.problem.id as problemId, COUNT(DISTINCT sp.user.id) as solvingCount "
		+ "FROM SolvedProblem sp GROUP BY sp.problem.id")
	List<ProblemSolvingCountProjection> countSolvingUsersByProblem();
}
