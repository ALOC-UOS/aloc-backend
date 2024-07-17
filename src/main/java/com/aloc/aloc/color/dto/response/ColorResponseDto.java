package com.aloc.aloc.color.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ColorResponseDto {
	private int coin;
	private String colorName;
	private String colorCode;
}
