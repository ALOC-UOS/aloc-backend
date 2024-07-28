package com.aloc.aloc.tag.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagSimpleDto {
	@Schema(description = "태그 ID", example = "33")
	private Long id;
	@Schema(description = "태그 한글 이름", example = "다이나믹 프로그래밍")
	private String koreanName;
	@Schema(description = "태그 영문 이름", example = "Dynamic Programming")
	private String englishName;
}
