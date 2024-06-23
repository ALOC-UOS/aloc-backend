package com.aloc.aloc.domain.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aloc.aloc.domain.user.dto.response.UserListResponseDto;
import com.aloc.aloc.domain.user.service.UserService;
import com.aloc.aloc.global.apipayload.ApiResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api2")
@Tag(name = "User API", description = "User API 입니다.")
public class UserController {

	private final UserService userService;

	@GetMapping("/")
	public String index() {
		return "hello world!";
	}

	@GetMapping("/users")
	public ApiResponse<UserListResponseDto> getUsers() {
		return ApiResponse.onSuccess(userService.getUsers());
	}
}
