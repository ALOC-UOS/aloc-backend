package com.aloc.aloc.problem.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserProblem;
import com.aloc.aloc.problemtype.enums.Routine;

@Repository
public interface UserProblemRepository extends JpaRepository<UserProblem, Long> {

	//	 문제를 푼 사용자 목록을 가져옵니다.
	List<UserProblem> findAllByProblemIdAndIsSolvedIsTrue(Long problemId);

	@Query("SELECT COUNT(DISTINCT up.user) "
		+ "FROM UserProblem up WHERE up.problem.id = :problemId "
		+ "AND up.isSolved = true AND up.season = :season")
	int countSolvingUsersByProblemId(@Param("problemId") Long problemId, @Param(
		"season") int season);

	boolean existsByUserIdAndProblemIdAndIsSolvedIsTrue(Long userId, Long problemId);

	Optional<UserProblem> findByUserIdAndProblemId(Long userId, Long problemId);

	// 시즌이 null 일때는 season에 상관없이 조회
	@Query("SELECT up "
		+ "FROM UserProblem up "
		+ "JOIN up.problem p "
		+ "WHERE up.user.id = :userId "
		+ "AND (:season IS NULL OR up.season = :season) "
		+ "AND up.isSolved = :isSolved "
		+ "AND (p.hidden = false)"
		+ "AND (p.problemType.routine = :routine) "
		+ "ORDER BY p.createdAt DESC")
	List<UserProblem> findAllByUserIdAndSeasonAndIsSolvedOrderBySolvedAtDesc(
		@Param("userId") Long userId,
		@Param("season") Integer season,
		@Param("isSolved") Boolean isSolved,
		@Param("routine") Routine routine);

	Optional<UserProblem> findTopByUserIdOrderBySolvedAtDesc(Long userId);

	@Query("SELECT COUNT(up) "
		+ "FROM UserProblem up "
		+ "WHERE up.problem "
		+ "IN :problems")
	int countByProblemsIn(@Param("problems") List<Problem> problems);

	@Query("SELECT COUNT(up) "
		+ "FROM UserProblem up "
		+ "WHERE up.user.id = :userId "
		+ "AND up.season = :season "
		+ "AND up.isSolved = :isSolved "
		+ "AND up.problem.problemType.routine = :routine")
	int countByUserIdAndSeasonAndIsSolved(Long userId, Integer season, boolean isSolved, Routine routine);

	Boolean existsByUserIdAndProblemIdAndSolvedAtAfter(Long userId, Long id, LocalDateTime localDateTime);
}
