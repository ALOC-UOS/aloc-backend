package com.aloc.aloc.problemtype.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aloc.aloc.problemtype.ProblemType;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;

@Repository
public interface ProblemTypeRepository extends JpaRepository<ProblemType, Long> {
	ProblemType findProblemTypeByCourseAndRoutine(Course course, Routine routine);
}