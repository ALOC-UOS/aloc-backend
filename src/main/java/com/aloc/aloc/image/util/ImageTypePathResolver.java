package com.aloc.aloc.image.util;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aloc.aloc.image.enums.ImageType;


@Component
public class ImageTypePathResolver {
	private final String baseUploadPath;

	public ImageTypePathResolver(@Value("${spring.upload.path:/upload/user}") String baseUploadPath) {
		this.baseUploadPath = baseUploadPath;
	}

	public Path resolvePath(ImageType imageType) {
		return Paths.get(baseUploadPath, imageType.name().toLowerCase()).toAbsolutePath().normalize();
	}
}
