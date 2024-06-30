package com.aloc.aloc.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aloc.aloc.auth.dto.UserLoginRequestDto;
import com.aloc.aloc.auth.service.AuthService;
import com.aloc.aloc.user.dto.request.UserRequestDto;

import io.swagger.v3.oas.annotations.Operation;
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
	@Operation(summary = "로그인", description = "gitubID를 통해 로그인합니다")
	public void login(@RequestBody @Valid UserLoginRequestDto userLoginRequestDto) {
		authService.login(userLoginRequestDto);
	}

	@PostMapping("/sign-up")
	@ApiResponse(
		responseCode = "200",
		description = "success"
	)
	@Operation(summary = "회원 가입", description = "새로운 회원으로 가입합니다.")
	public void signUp(@RequestBody @Valid UserRequestDto userRequestDto) {
		authService.signUp(userRequestDto);
	}

}
