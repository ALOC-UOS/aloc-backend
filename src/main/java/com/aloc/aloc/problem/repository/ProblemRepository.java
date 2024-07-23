package com.aloc.aloc.problem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;


@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {

	// hidden이 none인 문제들만 날짜 역순으로정렬해서 가져오기
	List<Problem> findAllByHiddenIsFalseOrderByCreatedAtDesc();

	// updatedAt이 현재 날짜 이후인 공개된 문제 찾기
	@Query("SELECT p FROM Problem p "
		+ "WHERE p.problemType.id = :problemTypeId AND p.hidden IS FALSE "
		+ "ORDER BY p.updatedAt DESC "
		+ "LIMIT 1")
	Problem findLatestPublicProblemByProblemTypeId(@Param("problemTypeId") Long problemTypeId);

	List<Problem> findAllByHiddenIsTrueAndProblemType_RoutineOrderByIdAsc(Routine routine);

	@Query("SELECT p FROM Problem p "
		+ "WHERE p.algorithm.season = :season "
		+ "AND p.algorithm.algorithmId = :algorithmId "
		+ "AND p.problemType.id = :problemTypeId "
		+ "AND p.hidden = false "
		+ "ORDER BY p.createdAt DESC")
	List<Problem> findVisibleProblemsByAlgorithmAndCourse(
		@Param("season") int season,
		@Param("algorithmId") int algorithmId,
		@Param("problemTypeId") Long problemTypeId
	);

	@Query("SELECT COUNT(p) FROM Problem p"
		+ " WHERE p.problemType.course = :course "
		+ "AND p.algorithm.season = :season "
		+ "AND p.problemType.routine = :routine AND p.hidden = false")
	int countAllByCourseAndRoutine(Integer season, Course course, Routine routine);

	@Query("SELECT p FROM Problem p "
		+ "WHERE p.algorithm.season = :season "
		+ "AND p.problemType.course = :course "
		+ "AND p.hidden = false ")
	List<Problem> findVisibleProblemsBySeasonAndCourse(
		@Param("season") int season,
		@Param("course") Course course
	);

	@Query("SELECT CASE WHEN COUNT(p) = 0 THEN true ELSE false END FROM Problem p "
		+ "WHERE p.problemId = :problemId "
		+ "AND p.problemType.course = :course "
		+ "AND (:season IS NULL OR p.algorithm.season = :season)")
	boolean notExistsByProblemIdAndCourseAndSeason(
		@Param("problemId") Integer problemId,
		@Param("course") Course course,
		@Param("season") Integer season
	);
}

