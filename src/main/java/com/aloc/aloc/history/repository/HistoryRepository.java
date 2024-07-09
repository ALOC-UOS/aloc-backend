package com.aloc.aloc.history.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aloc.aloc.history.History;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
	List<History> findByOrderByCreatedAtDesc();
}
