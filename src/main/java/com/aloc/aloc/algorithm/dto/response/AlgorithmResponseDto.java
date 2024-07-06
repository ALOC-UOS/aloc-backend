package com.aloc.aloc.algorithm.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlgorithmResponseDto {
	private int id;
	private int algorithmId;
	private String name;
	private int season;
	private Boolean hidden;
}
