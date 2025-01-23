package com.aloc.aloc.problemtag.repository;

import com.aloc.aloc.problemtag.ProblemTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemTagRepository extends JpaRepository<ProblemTag, Long> {}
