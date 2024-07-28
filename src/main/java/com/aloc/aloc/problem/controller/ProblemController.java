package com.aloc.aloc.problem.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api2")
@Tag(name = "Problem API", description = "Problem API 입니다.")
public class ProblemController {
	private final ProblemService problemService;
	private final ProblemFacade problemFacade;

	@GetMapping("/problems")
	@Operation(summary = "주차별 Daily 문제 조회 (7개)",
		description = "특정 시즌과 알고리즘의 공개된 문제 목록을 최근 순으로 정렬하여 조회합니다. (Daily)")
	@ApiResponse(
		responseCode = "200",
		description = "문제 목록 조회 성공",
		content = @Content(schema = @Schema(implementation = ProblemResponseDto.class))
	)
	public CustomApiResponse<List<ProblemResponseDto>> getProblemsByAlgorithmIdAndSeason(
		@Parameter(description = "시즌", required = true) @RequestParam int season,
		@Parameter(description = "코스", required = true) @RequestParam Course course,
		@Parameter(description = "알고리즘 ID", required = true) @RequestParam int algorithmId) {
		return CustomApiResponse.onSuccess(
			problemService.getVisibleDailyProblemsDtoByAlgorithmId(season, algorithmId, course)
		);
	}

	@GetMapping("problem/{problemId}/solved-users")
	@Operation(summary = "해당 문제를 푼 사용자 목록 조회", description = "해당 문제를 푼 사용자 목록을 조회합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "사용자 목록 조회 성공",
		content = @Content(schema = @Schema(implementation = SolvedUserResponseDto.class))
	)
	public CustomApiResponse<List<SolvedUserResponseDto>> getSolvedUserList(
		@Parameter(description = "문제 ID", required = true) @PathVariable() Long problemId
	) {
		return CustomApiResponse.onSuccess(problemFacade.getSolvedUserListByProblemId(problemId));
	}

	@GetMapping("/today-problem")
	@Operation(summary = "오늘의 문제 조회", description = "오늘의 문제를 조회합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "오늘의 문제 조회 성공",
		content = @Content(schema = @Schema(implementation = ProblemResponseDto.class))
	)
	public CustomApiResponse<ProblemResponseDto> getTodayProblem(
		@Parameter(description = "코스", required = true) @RequestParam Course course
	) {
		return CustomApiResponse.onSuccess(problemService.getTodayProblemDto(course));
	}

	@PostMapping("/today-problem/solved")
	@SecurityRequirement(name = "JWT Auth")
	@Operation(summary = "오늘 문제 풀이 상태 업데이트", description = "오늘의 문제를 풀었음을 확인합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "오늘의 문제 풀이 상태 업데이트 성공",
		content = @Content(schema = @Schema(implementation = String.class))
	)
	public CustomApiResponse<String> checkTodaySolved(
		@Parameter(hidden = true) @AuthenticationPrincipal User user
	) {
		return CustomApiResponse.onSuccess(problemFacade.checkTodaySolved(user.getUsername()));
	}

	@PostMapping("/problems/solved")
	@SecurityRequirement(name = "JWT Auth")
	@Operation(summary = "지나간 문제 풀이 여부 확인", description = "이미 지나간 문제들을 풀었음을 확인합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "지나간 문제 풀이 상태 업데이트 성공",
		content = @Content(schema = @Schema(implementation = String.class))
	)
	public CustomApiResponse<String> checkSolved(
		@Parameter(hidden = true) @AuthenticationPrincipal User user
	) {
		return CustomApiResponse.onSuccess(problemFacade.checkSolved(user.getUsername()));
	}

	@GetMapping("/weekly-problems")
	@SecurityRequirement(name = "JWT Auth")
	@Operation(summary = "이번주 Weekly 문제 조회 (5개)", description = "이번주 Weekly 문제를 조회합니다.")
	@ApiResponse(
		responseCode = "200",
		description = "오늘의 문제 조회 성공",
		content = @Content(schema = @Schema(implementation = ProblemResponseDto.class))
	)
	public CustomApiResponse<List<ProblemSolvedResponseDto>> getWeeklyCompletionStatus(
		@Parameter(hidden = true) @AuthenticationPrincipal User user
	) {
		return CustomApiResponse.onSuccess(problemFacade.getWeeklyProblem(user.getUsername()));
	}
}
