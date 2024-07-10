package com.aloc.aloc.problem.repository;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aloc.aloc.problem.entity.SolvedProblem;

@Repository
public interface SolvedProblemRepository extends JpaRepository<SolvedProblem, Long> {
	List<SolvedProblem> findAllByProblemId(Long problemId);

	@Query("SELECT COUNT(DISTINCT sp.user) FROM SolvedProblem sp WHERE sp.problem.id = :problemId")
	int countSolvingUsersByProblemId(@Param("problemId") Long problemId);

	boolean existsByUserIdAndProblemId(Long userId, Long problemId);

	List<SolvedProblem> findAllByUserIdAndProblemIdIn(Long userId, List<Long> problemId);

	List<SolvedProblem> findAllByUserIdOrderBySolvedAtDesc(Long userId);

	Optional<SolvedProblem> findByUserIdAndProblemId(Long userId, Long problemId);

	Integer countByUserId(Long userId);
}
