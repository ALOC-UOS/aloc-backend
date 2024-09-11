package com.aloc.aloc.image.dto;

import java.nio.file.Path;

import com.aloc.aloc.image.enums.ImageType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadedImageInfo {
	private ImageType imageType;
	private String imageName;
	private Path fullPath;
}