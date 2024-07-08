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
	List<Problem> findAllByHiddenIsNullOrderByCreatedAtDesc();

	// updatedAt이 현재 날짜 이후인 공개된 문제 찾기
	@Query("SELECT p FROM Problem p "
		+ "WHERE p.problemType.id = :problemTypeId AND p.hidden IS NULL "
		+ "ORDER BY p.updatedAt DESC "
		+ "LIMIT 1")
	Problem findLatestPublicProblemByProblemTypeId(@Param("problemTypeId") Long problemTypeId);

	Boolean existsByProblemIdAndProblemType_Course(Integer problemId, Course course);

	List<Problem> findAllByHiddenIsTrueAndProblemType_RoutineOrderByIdAsc(Routine routine);

	@Query("SELECT p FROM Problem p "
		+ "WHERE p.algorithm.season = :season AND p.algorithm.algorithmId = :algorithmId AND p.hidden = false "
		+ "ORDER BY p.createdAt DESC")
	List<Problem> findPublicProblemsByAlgorithm(@Param("season") int season, @Param("algorithmId") int algorithmId);
}
