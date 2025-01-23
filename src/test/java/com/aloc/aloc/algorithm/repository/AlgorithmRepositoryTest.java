package com.aloc.aloc.algorithm.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.aloc.aloc.algorithm.entity.Algorithm;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class AlgorithmRepositoryTest {
  @Autowired private AlgorithmRepository algorithmRepository;

  @Autowired EntityManager em;

  @BeforeEach
  void init() {
    List<Algorithm> algorithms = new ArrayList<>();
    Map<Integer, String> season1Algorithm = new HashMap<>();
    Map<Integer, String> season2Algorithm = new HashMap<>();

    // Map 자료형이라 name기준으로 정렬이되어 들어가는 듯 함 102 -> 24 -> 123
    season1Algorithm.put(123, "수학"); // 출력
    season1Algorithm.put(102, "구현");
    season1Algorithm.put(24, "다이나믹 프로그래밍(DP)");

    // 33 -> 7 -> 175
    season2Algorithm.put(175, "자료 구조");
    season2Algorithm.put(7, "그래프 이론"); // 출력
    season2Algorithm.put(33, "그리디 알고리즘");
    int idx = 1;
    for (Map.Entry<Integer, String> entry : season1Algorithm.entrySet()) {
      Algorithm algorithm =
          Algorithm.builder()
              .week(idx++)
              .algorithmId(entry.getKey())
              .season(1)
              .name(entry.getValue())
              .hidden(false)
              .build();
      algorithms.add(algorithm);
    }
    idx = 1;
    for (Map.Entry<Integer, String> entry : season2Algorithm.entrySet()) {
      Algorithm algorithm =
          Algorithm.builder()
              .week(idx++)
              .algorithmId(entry.getKey())
              .season(2)
              .name(entry.getValue())
              .hidden(true)
              .build();
      algorithms.add(algorithm);
    }
    algorithmRepository.saveAll(algorithms);
    em.flush();
    em.clear();
  }

  @Test
  @DisplayName("특정 season 중에서 hidden이 true인 것 중 첫 번째 항목 가져오기")
  void findFirstBySeasonAndHiddenTrueOrderByIdAsc() {
    Optional<Algorithm> result =
        algorithmRepository.findFirstBySeasonAndHiddenTrueOrderByWeekAsc(2);

    assertNotNull(result);
    assertEquals("그리디 알고리즘", result.get().getName());
  }

  @Test
  @DisplayName("특정 season 중에서 hidden이 false인 것 중 가장 마지막 항목 가져오기")
  void findFirstBySeasonAndHiddenFalseOrderByIdDesc() {
    Optional<Algorithm> result =
        algorithmRepository.findFirstBySeasonAndHiddenFalseOrderByWeekDesc(1);

    assertNotNull(result);
    assertEquals("수학", result.get().getName());
  }

  @Test
  @DisplayName("최신 순으로 특정 시즌의 공개된 알고리즘 목록 불러오기")
  void findAllBySeasonOrderByCreatedAtDesc() {
    List<Algorithm> result = algorithmRepository.findAllBySeasonAndHiddenFalseOrderByWeekDesc(1);

    assertNotNull(result);
    assertEquals(3, result.get(0).getWeek());
  }

  @Test
  @DisplayName("최신 순으로 모든 공개된 알고리즘 목록 불러오기")
  void findAllByOrderByCreatedAtDesc() {
    List<Algorithm> result = algorithmRepository.findAllByHiddenIsFalseOrderByWeekDesc();

    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals(3, result.get(0).getWeek());
    assertEquals(false, result.get(0).getHidden());
  }
}
