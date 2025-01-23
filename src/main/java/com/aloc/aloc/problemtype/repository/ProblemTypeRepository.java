package com.aloc.aloc.problemtype.repository;

import com.aloc.aloc.problemtype.ProblemType;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemTypeRepository extends JpaRepository<ProblemType, Long> {
  Optional<ProblemType> findProblemTypeByCourseAndRoutine(Course course, Routine routine);

  Optional<ProblemType> findByCourseAndRoutine(Course course, Routine routine);
}
