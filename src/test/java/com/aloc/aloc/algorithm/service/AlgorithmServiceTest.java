package com.aloc.aloc.algorithm.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import com.aloc.aloc.algorithm.dto.response.AlgorithmResponseDto;
import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.repository.AlgorithmRepository;

@ExtendWith(MockitoExtension.class)
@Transactional
class AlgorithmServiceTest {
	@Mock
	private AlgorithmRepository algorithmRepository;
	@InjectMocks
	private AlgorithmService algorithmService;

	private List<Algorithm> algorithms;
	@BeforeEach
	void setUp() {
		algorithms = new ArrayList<>();
		Algorithm algorithm1 = new Algorithm(3, 5, "알고리즘 5", 2, false);
		Algorithm algorithm2 = new Algorithm(2, 4, "알고리즘 4", 2, false);
		Algorithm algorithm3 = new Algorithm(1, 3, "알고리즘 3", 2, false);
		Algorithm algorithm4 = new Algorithm(2, 2, "알고리즘 2", 1, false);
		Algorithm algorithm5 = new Algorithm(1, 1, "알고리즘 1", 1, false);
		algorithms.add(algorithm1);
		algorithms.add(algorithm2);
		algorithms.add(algorithm3);
		algorithms.add(algorithm4);
		algorithms.add(algorithm5);
	}

	@Test
	@DisplayName("모든 알고리즘 조회 성공")
	void getAlgorithms() {
		// given
		when(algorithmRepository.findAllByOrderByCreatedAtDesc())
			.thenReturn(algorithms);

		// when
		List<AlgorithmResponseDto> result = algorithmService.getAlgorithms();

		// then
		assertThat(result).hasSize(2);

		AlgorithmResponseDto season1 = result.stream().filter(r -> r.getSeason() == 1).findFirst().orElse(null);
		assertThat(season1).isNotNull();
		assert season1 != null;
		assertThat(season1.getAlgorithms()).hasSize(2);
		AssertionsForClassTypes.assertThat(season1.getAlgorithms().get(0).getId()).isEqualTo(2);

		AlgorithmResponseDto season2 = result.stream().filter(r -> r.getSeason() == 2).findFirst().orElse(null);
		assertThat(season2).isNotNull();
		assert season2 != null;
		assertThat(season2.getAlgorithms()).hasSize(3);
		assertThat(season2.getAlgorithms().get(0).getId()).isEqualTo(3);
	}

	@Test
	@DisplayName("특정 시즌 알고리즘 조회 성공")
	void getAlgorithmsBySeason() {
		// given
		int season = 2;
		when(algorithmRepository.findAllBySeasonOrderByIdDesc(season))
			.thenReturn(algorithms.subList(0, 3));

		// when
		AlgorithmResponseDto result = algorithmService.getAlgorithmsBySeason(season);

		// then
		AssertionsForClassTypes.assertThat(result).isNotNull();
		assertThat(result.getAlgorithms()).hasSize(3);
		AssertionsForClassTypes.assertThat(result.getAlgorithms().get(0).getId()).isEqualTo(3);
	}
}
