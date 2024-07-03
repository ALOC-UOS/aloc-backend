package com.aloc.aloc.algorithm.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.transaction.annotation.Transactional;

import com.aloc.aloc.algorithm.Algorithm;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
@ActiveProfiles("dev")
@EnabledIf(expression = "#{environment.acceptsProfiles('dev')}", loadContext = true)
class AlgorithmRepositoryTest {

	@Autowired
	private AlgorithmRepository algorithmRepository;
	@Autowired
	EntityManager em;

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
		for (Map.Entry<Integer, String> entry : season1Algorithm.entrySet()) {
			Algorithm algorithm = Algorithm.builder()
				.algorithmId(entry.getKey())
				.season(1)
				.name(entry.getValue())
				.hidden(false)
				.build();
			algorithms.add(algorithm);
		}
		for (Map.Entry<Integer, String> entry : season2Algorithm.entrySet()) {
			Algorithm algorithm = Algorithm.builder()
				.algorithmId(entry.getKey())
				.season(2)
				.name(entry.getValue())
				.hidden(true)
				.build();
			algorithms.add(algorithm);
		}
		algorithmRepository.saveAll(algorithms);
	}

	@Test
	@DisplayName("특정 season 중에서 hidden이 true인 것 중 첫 번째 항목 가져오기")
	void findFirstBySeasonAndHiddenTrueOrderByIdAsc() {
		Optional<Algorithm> result = algorithmRepository.findFirstBySeasonAndHiddenTrueOrderByIdAsc(2);

		assertNotNull(result);
		assertEquals("그리디 알고리즘", result.get().getName());
	}

	@Test
	@DisplayName("특정 season 중에서 hidden이 false인 것 중 가장 마지막 항목 가져오기")
	void findFirstBySeasonAndHiddenFalseOrderByIdDesc() {
		Optional<Algorithm> result = algorithmRepository.findFirstBySeasonAndHiddenFalseOrderByIdDesc(1);

		assertNotNull(result);
		assertEquals("수학", result.get().getName());
	}
}
