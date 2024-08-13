package com.aloc.aloc.coinhistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aloc.aloc.coinhistory.CoinHistory;

public interface CoinHistoryRepository extends JpaRepository<CoinHistory, Long> {

}
