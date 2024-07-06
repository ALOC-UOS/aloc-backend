package com.aloc.aloc.algorithm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aloc.aloc.algorithm.Algorithm;

@Repository
public interface AlgorithmRepository extends JpaRepository<Algorithm, Long> {
	// 특정 season 중에서 hidden이 true인 것 중 첫 번째 항목 가져오기
	Optional<Algorithm> findFirstBySeasonAndHiddenTrueOrderByIdAsc(int season);

	// 특정 season 중에서 hidden이 false인 것 중 가장 마지막 항목 가져오기
	Optional<Algorithm> findFirstBySeasonAndHiddenFalseOrderByIdDesc(int season);

	List<Algorithm> findAllBySeasonOrderByIdDesc(int season);
}
