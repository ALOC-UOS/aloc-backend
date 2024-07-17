package com.aloc.aloc.problem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aloc.aloc.problem.entity.UserProblem;

// TODO: 메소드 확인하기
@Repository
public interface UserProblemRepository extends JpaRepository<UserProblem, Long> {
	List<UserProblem> findAllByProblemId(Long problemId);

	@Query("SELECT COUNT(DISTINCT up.user) "
		+ "FROM UserProblem up WHERE up.problem.id = :problemId "
		+ "AND up.isSolved = true AND up.season = :season")
	int countSolvingUsersByProblemId(@Param("problemId") Long problemId, @Param(
		"season") int season);

	boolean existsByUserIdAndProblemId(Long userId, Long problemId);

	List<UserProblem> findAllByUserIdAndProblemIdIn(Long userId, List<Long> problemId);

	List<UserProblem> findAllByUserIdOrderBySolvedAtDesc(Long userId);

	Optional<UserProblem> findByUserIdAndProblemId(Long userId, Long problemId);

	Integer countByUserId(Long userId);

	List<UserProblem> findAllByUserIdAndSeasonAndIsSolvedIsTrueOrderBySolvedAtDesc(Long userId, int season);
}
