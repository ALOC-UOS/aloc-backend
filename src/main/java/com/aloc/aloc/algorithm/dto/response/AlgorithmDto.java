package com.aloc.aloc.algorithm.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.aloc.aloc.algorithm.entity.Algorithm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AlgorithmDto {
	private int week;
	private int algorithmId;
	private String name;
	private Boolean hidden;

	public static List<AlgorithmDto> listOf(List<Algorithm> algorithms) {
		return algorithms.stream()
			.map(algorithm -> AlgorithmDto.builder()
				.week(algorithm.getWeek())
				.algorithmId(algorithm.getAlgorithmId())
				.name(algorithm.getName())
				.hidden(algorithm.getHidden())
				.build())
			.collect(Collectors.toList());
	}
}
