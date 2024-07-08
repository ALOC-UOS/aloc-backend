package com.aloc.aloc.algorithm.dto.response;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.aloc.aloc.algorithm.entity.Algorithm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AlgorithmResponseDto {
	private int id;
	private int algorithmId;
	private String name;
	private int season;
	private Boolean hidden;

	public static List<AlgorithmResponseDto> listOf(List<Algorithm> algorithms) {
		AtomicInteger index = new AtomicInteger(algorithms.size());
		return algorithms.stream()
			.map(algorithm -> AlgorithmResponseDto.builder()
				.id(index.getAndDecrement())
				.algorithmId(algorithm.getAlgorithmId())
				.name(algorithm.getName())
				.season(algorithm.getSeason())
				.hidden(algorithm.getHidden())
				.build())
			.collect(Collectors.toList());
	}
}
