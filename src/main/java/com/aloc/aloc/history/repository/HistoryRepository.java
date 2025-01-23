package com.aloc.aloc.history.repository;

import com.aloc.aloc.history.History;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
  List<History> findByOrderByCreatedAtDesc();
}
