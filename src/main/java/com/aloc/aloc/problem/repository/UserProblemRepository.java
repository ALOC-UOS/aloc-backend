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

	// 문제를 푼 사용자 목록을 가져옵니다.
	List<UserProblem> findAllByProblemIdAndIsSolvedIsTrue(Long problemId);

	@Query("SELECT COUNT(DISTINCT up.user) "
		+ "FROM UserProblem up WHERE up.problem.id = :problemId "
		+ "AND up.isSolved = true AND up.season = :season")
	int countSolvingUsersByProblemId(@Param("problemId") Long problemId, @Param(
		"season") int season);

	boolean existsByUserIdAndProblemIdAndIsSolvedIsTrue(Long userId, Long problemId);

	List<UserProblem> findAllByUserIdAndProblemIdIn(Long userId, List<Long> problemId);

	Optional<UserProblem> findByUserIdAndProblemId(Long userId, Long problemId);

	Integer countByUserId(Long userId);

	@Query("SELECT up FROM UserProblem up WHERE up.user.id = :userId "
		+ "AND (:season IS NULL OR up.season = :season) "
		+ "AND up.isSolved = :isSolved "
		+ "ORDER BY up.solvedAt DESC")
	List<UserProblem> findAllByUserIdAndSeasonAndIsSolvedOrderBySolvedAtDesc(
		@Param("userId") Long userId,
		@Param("season") Integer season,
		@Param("isSolved") Boolean isSolved
	);
}
