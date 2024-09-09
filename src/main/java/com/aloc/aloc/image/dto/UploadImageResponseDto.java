package com.aloc.aloc.image.dto;

import lombok.Builder;
public record UploadImageResponseDto(String message, String fileName) {

	@Builder
	public UploadImageResponseDto {
	}
}
