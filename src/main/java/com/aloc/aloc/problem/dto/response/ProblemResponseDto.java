package com.aloc.aloc.problem.dto.response;

import java.util.List;

import com.aloc.aloc.tag.dto.TagSimpleDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemResponseDto {
	private Long id;
	private String title;
	private List<TagSimpleDto> tags;
	private Integer difficulty;
	private Integer solvingCount;
}
