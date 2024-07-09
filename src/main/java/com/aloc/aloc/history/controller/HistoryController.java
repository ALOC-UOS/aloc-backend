package com.aloc.aloc.history.controller;


import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.history.dto.response.HistoryResponseDto;
import com.aloc.aloc.history.service.HistoryService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api2/history")
public class HistoryController {

	private final HistoryService historyService;

	@GetMapping()
	@Operation(summary = "히스토리 목록 조회", description = "최근 추가된 순서로 모든 히스토리 목록 조회")
	public CustomApiResponse<List<HistoryResponseDto>> getHistories() {
		return CustomApiResponse.onSuccess(historyService.getHistories());
	}
}
