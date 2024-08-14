package com.aloc.aloc.coinhistory.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aloc.aloc.coinhistory.dto.response.CoinHistoryResponseDto;
import com.aloc.aloc.coinhistory.service.CoinHistoryService;
import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api2/coin-history")
@Tag(name = "CoinHistory API", description = "CoinHistory API 입니다.")
public class CoinHistoryController {
	private final CoinHistoryService coinHistoryService;
	private final UserService userService;
	@GetMapping("")
	@SecurityRequirement(name = "JWT Auth")
	@Operation(summary = "유저 코인 히스토리 조회", description = "유저의 코인 히스토리 목록을 조회합니다.")
	public CustomApiResponse<CoinHistoryResponseDto> getCoinHistory(
		@Parameter(hidden = true) @AuthenticationPrincipal User user) {
		return CustomApiResponse.onSuccess(coinHistoryService
			.getUserCoinHistory(userService.findUser(user.getUsername())));
	}
}
