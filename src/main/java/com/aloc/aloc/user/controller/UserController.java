package com.aloc.aloc.user.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.service.UserFacade;
import com.aloc.aloc.user.service.UserRegistrationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api2")
@Tag(name = "User API", description = "User API 입니다.")
public class UserController {

	private final UserFacade userFacade;
	private final UserRegistrationService userRegistrationService;

	@GetMapping("/users")
	@Operation(summary = "유저 목록 조회", description = "전체 유저 목록을 조회합니다.")
	public CustomApiResponse<List<UserDetailResponseDto>> getUsers() {
		return CustomApiResponse.onSuccess(userFacade.getUsers());
	}

	@SecurityRequirement(name = "JWT Auth")
	@SecurityRequirement(name = "Refresh Token")
	@PostMapping("/authorize/{githubId}")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "스터디 멤버로 등록되었습니다."),
		@ApiResponse(responseCode = "400", description = "해당 깃허브 아이디로 가입된 사용자가 없습니다."),
		@ApiResponse(responseCode = "400", description = "이미 등록된 멤버입니다.")
	})
	@Operation(summary = "유저 추가", description = "스터디 멤버로 추가합니다.")
	public CustomApiResponse<String> addUser(
		@Parameter(hidden = true) @AuthenticationPrincipal User user,
		@Parameter(description = "깃허브 ID", required = true) @PathVariable("githubId") String githubId
	) {
		return CustomApiResponse.onSuccess(userRegistrationService.addUser(user.getUsername(), githubId));
	}
}
