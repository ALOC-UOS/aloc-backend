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

import com.aloc.aloc.image.dto.UploadImageResponseDto;
import com.aloc.aloc.image.dto.UploadedFileInfo;
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
	public ResponseEntity<UploadImageResponseDto> uploadItemImage(
		@RequestParam("file") MultipartFile file) throws FileUploadException {
		UploadedFileInfo uploadedImage = imageUploadService.uploadImage(file, ImageType.ITEM, null);
		UploadImageResponseDto responseDto = new UploadImageResponseDto(
			"Item File uploaded successfully",
			uploadedImage.getFileName()
		);
		return ResponseEntity.ok(responseDto);
	}

	@SecurityRequirement(name = "JWT Auth")
	@PostMapping(value = "/upload/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UploadImageResponseDto> uploadProfileImage(
		@RequestParam("file") MultipartFile file,
		@Parameter(hidden = true) @AuthenticationPrincipal User user) throws FileUploadException {
		Map<String, Object> metadata = new HashMap<>();
		metadata.put("username", user.getUsername());

		UploadedFileInfo uploadedImage = imageUploadService.uploadImage(file, ImageType.PROFILE,
			metadata);
		UploadImageResponseDto responseDto = new UploadImageResponseDto(
			"Profile File uploaded successfully",
			uploadedImage.getFileName()
		);
		return ResponseEntity.ok(responseDto);
	}
}
