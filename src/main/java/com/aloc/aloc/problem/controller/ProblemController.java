package com.aloc.aloc.problem.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import com.aloc.aloc.problem.service.ProblemFacade;
import com.aloc.aloc.problem.service.ProblemService;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.user.dto.response.SolvedUserResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api2/problem")
@Tag(name = "Problem API", description = "Problem API 입니다.")
public class ProblemController {
	private final ProblemService problemService;
	private final ProblemFacade problemFacade;

	@GetMapping()
	@Operation(summary = "문제 목록 조회", description = "최근 생성일 기준으로 정렬하여 전체 문제 목록을 조회합니다.")
	public CustomApiResponse<List<ProblemResponseDto>> getProblems() {
		return CustomApiResponse.onSuccess(problemService.getVisibleProblemsWithSolvingCount());
	}

	@GetMapping("/solved-user/{problemId}")
	@Operation(summary = "해당 문제를 푼 사용자 목록 조회", description = "해당 문제를 푼 사용자 목록을 조회합니다.")
	public CustomApiResponse<List<SolvedUserResponseDto>> getSolvedUserList(
		@Parameter(description = "문제 ID", required = true) @PathVariable() Long problemId
	) {
		return CustomApiResponse.onSuccess(problemFacade.getSolvedUserListByProblemId(problemId));
	}

	@GetMapping("/today/{course}")
	@Operation(summary = "오늘의 문제 조회", description = "오늘의 문제를 조회합니다.")
	public CustomApiResponse<ProblemResponseDto> getTodayProblem(
		@Parameter(description = "코스 ID", required = true) @PathVariable() Course course
	) {
		return CustomApiResponse.onSuccess(problemService.findTodayProblemByCourse(course));
	}

	@SecurityRequirement(name = "JWT Auth")
	@PutMapping("/solved")
	@Operation(summary = "문제 풀이 여부 확인", description = "해당 문제를 풀었음을 확인합니다.")
	public CustomApiResponse<String> checkSolved(
		@Parameter(hidden = true) @AuthenticationPrincipal User user
	) throws IOException {
		return CustomApiResponse.onSuccess(problemFacade.checkSolved(user.getUsername()));
	}

	@SecurityRequirement(name = "JWT Auth")
	@GetMapping("/weekly/status")
	@Operation(summary = "이번주 Weekly 풀이 현황 조회", description = "이번주 Weekly 문제 풀이 현황을 조회합니다.")
	public CustomApiResponse<List<ProblemSolvedResponseDto>> getWeeklyCompletionStatus(
		@Parameter(hidden = true) @AuthenticationPrincipal User user
	) {
		return CustomApiResponse.onSuccess(problemFacade.getWeeklyCompletionStatus(user.getUsername()));
	}

	@GetMapping("/unsolved/user/{githubId}")
	@Operation(summary = "유저의 풀지 않은 문제 조회", description = "유저가 풀지 않은 문제를 조회합니다.")
	public CustomApiResponse<List<ProblemSolvedResponseDto>> getUnsolvedProblemList(
		@Parameter(required = true) @PathVariable() String githubId
	) {
		return CustomApiResponse.onSuccess(problemFacade.getUnsolvedProblemListByUser(githubId));
	}
}
