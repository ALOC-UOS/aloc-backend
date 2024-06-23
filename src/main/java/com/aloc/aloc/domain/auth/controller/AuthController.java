package com.aloc.aloc.domain.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aloc.aloc.domain.auth.dto.UserLoginRequestDto;
import com.aloc.aloc.domain.auth.service.AuthService;
import com.aloc.aloc.domain.user.dto.request.UserRequestDto;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api2")
@Tag(name = "Auth API", description = "인증 관련 API 입니다.")
public class AuthController {
	private final AuthService authService;
	@PostMapping("/login")
	@ApiResponse(
		responseCode = "200",
		description = "No Content"
	)
	public void login(@RequestBody @Valid UserLoginRequestDto userLoginRequestDto) {
		authService.login(userLoginRequestDto);
	}

	@PostMapping("/sign-up")
	@ApiResponse(
		responseCode = "200",
		description = "success"
	)
	public void signUp(@RequestBody @Valid UserRequestDto userRequestDto) {
		authService.signUp(userRequestDto);
	}

}
