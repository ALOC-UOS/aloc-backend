package com.aloc.aloc.algorithm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.entity.AlgorithmPrimaryKey;

@Repository
public interface AlgorithmRepository extends JpaRepository<Algorithm, AlgorithmPrimaryKey> {
	// 특정 season 중에서 hidden이 true인 것 중 첫 번째 항목 가져오기
	Optional<Algorithm> findFirstBySeasonAndHiddenTrueOrderByCreatedAtAsc(int season);

	// 특정 season 중에서 hidden이 false인 것 중 가장 마지막 항목 가져오기
	Optional<Algorithm> findFirstBySeasonAndHiddenFalseOrderByCreatedAtDesc(int season);

	List<Algorithm> findAllByHiddenIsFalseOrderByCreatedAtDesc();

	List<Algorithm> findAllBySeasonAndHiddenFalseOrderByCreatedAtDesc(int season);

	@Query("SELECT a FROM Algorithm a WHERE a.name = :algorithmName AND a.season = :season")
	Optional<Algorithm> findAlgorithmByNameAndSeason(
		@Param("algorithmName") String algorithmName,
		@Param("season") Integer season);
}
