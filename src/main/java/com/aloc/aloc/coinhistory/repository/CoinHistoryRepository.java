package com.aloc.aloc.coinhistory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aloc.aloc.coinhistory.CoinHistory;

@Repository
public interface CoinHistoryRepository extends JpaRepository<CoinHistory, Long> {
	List<CoinHistory> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
