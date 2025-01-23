package com.aloc.aloc.history.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.aloc.aloc.history.History;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class HistoryRepositoryTest {
  @Autowired private EntityManager em;

  @Autowired private HistoryRepository historyRepository;

  @BeforeEach
  void setUp() {
    List<History> histories = new ArrayList<>();
    History history1 = new History(1L, null, "홍길동", "plusMember", null);
    History history2 = new History(2L, null, "홍길동", "changeRank", 33);
    History history3 = new History(3L, null, "홍길동", "changeRank", 32);

    history1.setCreatedAt(LocalDateTime.now().minusDays(1));
    history2.setCreatedAt(LocalDateTime.now().minusDays(1));
    history3.setCreatedAt(LocalDateTime.now());

    histories.add(history1);
    histories.add(history2);
    histories.add(history3);
    historyRepository.saveAll(histories);
    em.flush();
    em.clear();
  }

  @Test
  @DisplayName("최근 추가된 순서대로 히스토리 목록 조회하기")
  void findByOrderByCreatedAtDesc() {
    List<History> historyList = historyRepository.findByOrderByCreatedAtDesc();

    assertThat(historyList).hasSize(3);
    assertThat(historyList.get(0).getId()).isEqualTo(3L);
  }
}
