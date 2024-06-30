package com.aloc.aloc.tag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagSimpleDto {
	private Long id;
	private String koreanName;
	private String englishName;
}
