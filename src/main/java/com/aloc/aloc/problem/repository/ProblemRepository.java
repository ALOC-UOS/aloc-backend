package com.aloc.aloc.problem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aloc.aloc.problem.Problem;


@Repository
public interface ProblemRepository extends JpaRepository<Problem, String> {

	// hidden이 none인 문제들만 날짜 역순으로정렬해서 가져오기
	List<Problem> findAllByHiddenIsNullOrderByCreatedAtDesc();
}
