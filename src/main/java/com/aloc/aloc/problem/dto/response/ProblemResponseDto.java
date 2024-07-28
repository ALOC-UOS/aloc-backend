package com.aloc.aloc.problem.dto.response;

import java.util.List;

import com.aloc.aloc.tag.dto.TagSimpleDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProblemResponseDto {
	@Schema(description = "문제 ID", example = "1080")
	private Integer id;
	@Schema(description = "문제 제목", example = "A와 B ")
	private String title;
	@Schema(description = "문제 태그")
	private List<TagSimpleDto> tags;
	@Schema(description = "문제 난이도", example = "8")
	private Integer difficulty;
	@Schema(description = "푼 사람 수", example = "10")
	private Integer solvingCount;
}
