package com.aloc.aloc.image.dto;

import lombok.Builder;

public record UploadedImageResponseDto(String message, String fileName) {

  @Builder
  public UploadedImageResponseDto {}
}
