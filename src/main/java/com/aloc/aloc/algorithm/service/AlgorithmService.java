package com.aloc.aloc.algorithm.service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aloc.aloc.algorithm.dto.response.AlgorithmDto;
import com.aloc.aloc.algorithm.dto.response.AlgorithmResponseDto;
import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.repository.AlgorithmRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlgorithmService {

	private final AlgorithmRepository algorithmRepository;

	@Value("${app.season}")
	private int currentSeason;

	public List<AlgorithmResponseDto> getAlgorithms() {
		List<Algorithm> algorithms = algorithmRepository.findAllByHiddenIsFalseOrderByCreatedAtDesc();
		// 시즌별로 그룹화
		Map<Integer, List<AlgorithmDto>> groupedBySeason = algorithms.stream()
				.collect(Collectors.groupingBy(
						Algorithm::getSeason,
						Collectors.mapping(algorithm -> AlgorithmDto.builder()
										.week(algorithm.getWeek())
										.algorithmId(algorithm.getAlgorithmId())
										.name(algorithm.getName())
										.build(),
								Collectors.toList())
				));

		// 그룹화된 결과를 AlgorithmResponseDto 리스트로 변환
		return groupedBySeason.entrySet().stream().map(entry -> AlgorithmResponseDto.builder()
						.season(entry.getKey())
						.algorithms(entry.getValue())
						.build())
				.collect(Collectors.toList());
	}

	public AlgorithmResponseDto getAlgorithmsBySeason(int season) {
		List<Algorithm> algorithms = algorithmRepository.findAllBySeasonAndHiddenFalseOrderByCreatedAtDesc(season);
		return AlgorithmResponseDto.builder()
						.season(season)
						.algorithms(AlgorithmDto.listOf(algorithms))
						.build();
	}

	public Optional<Algorithm> getAlgorithmBySeason(int season) {
		return algorithmRepository.findFirstBySeasonAndHiddenFalseOrderByCreatedAtDesc(season);
	}

	public void saveAlgorithm(Algorithm algorithm) {
		algorithmRepository.save(algorithm);
	}

	public Algorithm findWeeklyAlgorithm() {
		return algorithmRepository.findFirstBySeasonAndHiddenTrueOrderByCreatedAtAsc(currentSeason)
			.orElseThrow(() -> new NoSuchElementException("해당 시즌의 공개되지 않은 알고리즘이 존재하지 않습니다."));
	}

	public Algorithm findDailyAlgorithm() {
		return algorithmRepository.findFirstBySeasonAndHiddenFalseOrderByCreatedAtDesc(currentSeason)
			.orElseThrow(() -> new NoSuchElementException("공개된 알고리즘이 존재하지 않습니다."));
	}

	public Algorithm getAlgorithmByName(String algorithmName) {
		return algorithmRepository.findAlgorithmByNameAndSeason(algorithmName, currentSeason)
			.orElseThrow(() -> new NoSuchElementException("해당 알고리즘이 존재하지 않습니다."));
	}
}
