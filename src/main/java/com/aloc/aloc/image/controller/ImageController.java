package com.aloc.aloc.image.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aloc.aloc.image.service.ImageService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api2/image")
@Tag(name = "Image API", description = "Image API 입니다.")
public class ImageController {
	private final ImageService imageService;
}
