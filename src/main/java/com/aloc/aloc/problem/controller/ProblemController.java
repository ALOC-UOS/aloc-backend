package com.aloc.aloc.problem.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.service.ProblemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api2/problem")
@Tag(name = "Problem API", description = "Problem API 입니다.")
public class ProblemController {
	private final ProblemService problemService;

	@GetMapping()
	@Operation(summary = "문제 목록 조회", description = "최근 생성일 기준으로 정렬하여 전체 문제 목록을 조회합니다.")
	public CustomApiResponse<List<ProblemResponseDto>> getProblems() {
		return CustomApiResponse.onSuccess(problemService.getProblems());
	}
}
