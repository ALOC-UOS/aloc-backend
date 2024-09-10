package com.aloc.aloc.image.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aloc.aloc.image.dto.UploadedImageInfo;
import com.aloc.aloc.image.dto.UploadedImageResponseDto;
import com.aloc.aloc.image.enums.ImageType;
import com.aloc.aloc.image.service.ImageUploadService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api2/images")
@Tag(name = "Image API", description = "Image API 입니다.")
public class ImageController {
	private final ImageUploadService imageUploadService;

	@PostMapping(value = "/upload/items", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UploadedImageResponseDto> uploadItemImage(
		@RequestParam("file") MultipartFile file) throws FileUploadException {
		UploadedImageInfo uploadedImage = imageUploadService.uploadImage(file, ImageType.ITEM, null);
		UploadedImageResponseDto responseDto = new UploadedImageResponseDto(
			"Item File uploaded successfully",
			uploadedImage.getImageName()
		);
		return ResponseEntity.ok(responseDto);
	}

	@SecurityRequirement(name = "JWT Auth")
	@PostMapping(value = "/upload/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UploadedImageResponseDto> uploadProfileImage(
		@RequestParam("file") MultipartFile file,
		@Parameter(hidden = true) @AuthenticationPrincipal User user) throws FileUploadException {
		Map<String, Object> metadata = new HashMap<>();
		metadata.put("username", user.getUsername());

		UploadedImageInfo uploadedImage = imageUploadService.uploadImage(file, ImageType.PROFILE,
			metadata);
		UploadedImageResponseDto responseDto = new UploadedImageResponseDto(
			"Profile File uploaded successfully",
			uploadedImage.getFileName()
		);
		return ResponseEntity.ok(responseDto);
	}
}
