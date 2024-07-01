package com.aloc.aloc.problemtag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aloc.aloc.problemtag.ProblemTag;

@Repository
public interface ProblemTagRepository extends JpaRepository<ProblemTag, Long> {
}
