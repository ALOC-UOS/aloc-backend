package com.aloc.aloc.algorithm.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
public class AlgorithmResponseDto {
	private Integer season;
	private List<AlgorithmDto> algorithms;

	@Builder
	public AlgorithmResponseDto(Integer season, List<AlgorithmDto> algorithms) {
		this.season = season;
		this.algorithms = algorithms;
	}
}
