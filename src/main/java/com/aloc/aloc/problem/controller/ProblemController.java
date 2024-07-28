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
import com.aloc.aloc.problemtype.enums.Routine;
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

	@GetMapping("/season/{season}/algorithmId/{algorithmId}/course/{course}")
	@Operation(summary = "알고리즘 Id와 시즌으로 문제 목록 조회 (Daily)",
		description = "특정 시즌과 알고리즘의 공개된 문제 목록을 최근 순으로 정렬하여 조회합니다. (Daily)")
	public CustomApiResponse<List<ProblemResponseDto>> getProblemsByAlgorithmIdAndSeason(
		@Parameter(description = "시즌", required = true) @PathVariable int season,
		@Parameter(description = "코스", required = true) @PathVariable() Course course,
		@Parameter(description = "알고리즘 ID", required = true) @PathVariable int algorithmId) {
		return CustomApiResponse.onSuccess(
			problemService.getVisibleDailyProblemsDtoByAlgorithmId(season, algorithmId, course)
		);
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
		@Parameter(description = "코스", required = true) @PathVariable() Course course
	) {
		return CustomApiResponse.onSuccess(problemService.getTodayProblemDto(course));
	}

	@SecurityRequirement(name = "JWT Auth")
	@PostMapping("/today/solved")
	@Operation(summary = "오늘 문제 풀이 여부 확인", description = "오늘의 문제를 풀었음을 확인합니다.")
	public CustomApiResponse<String> checkTodaySolved(
		@Parameter(hidden = true) @AuthenticationPrincipal User user
	) {
		return CustomApiResponse.onSuccess(problemFacade.checkTodaySolved(user.getUsername()));
	}

	@SecurityRequirement(name = "JWT Auth")
	@PostMapping("/solved")
	@Operation(summary = "문제 풀이 여부 확인", description = "해당 문제를 풀었음을 확인합니다.")
	public CustomApiResponse<String> checkSolved(
		@Parameter(hidden = true) @AuthenticationPrincipal User user
	) {
		return CustomApiResponse.onSuccess(problemFacade.checkSolved(user.getUsername()));
	}

	@SecurityRequirement(name = "JWT Auth")
	@GetMapping("/weekly")
	@Operation(summary = "이번주 Weekly 문제 조회 (5개)", description = "이번주 Weekly 문제를 조회합니다.")
	public CustomApiResponse<List<ProblemSolvedResponseDto>> getWeeklyCompletionStatus(
		@Parameter(hidden = true) @AuthenticationPrincipal User user
	) {
		return CustomApiResponse.onSuccess(problemFacade.getWeeklyProblem(user.getUsername()));
	}

	@GetMapping("/unsolved/user/{githubId}/routine/{routine}")
	@Operation(summary = "유저의 풀지 않은 문제 조회", description = "유저가 풀지 않은 문제를 조회합니다. 시즌이 null이면 모든 시즌을 조회합니다.")
	public CustomApiResponse<List<ProblemSolvedResponseDto>> getUnsolvedProblemList(
		@Parameter(required = true) @PathVariable() String githubId,
		@Parameter(description = "루틴", required = true) @PathVariable() Routine routine,
		@Parameter(description = "조회할 시즌 (선택, 기본값: 모든 시즌)") @RequestParam(required = false) Integer season) {
		return CustomApiResponse.onSuccess(problemFacade.getUnsolvedProblemListByUser(githubId, season, routine));
	}

	@GetMapping("/solved/user/{githubId}/routine/{routine}")
	@Operation(summary = "유저의 이미 푼 문제 조회", description = "유저가 푼 문제를 조회합니다. 시즌이 null이면 모든 시즌을 조회합니다.")
	public CustomApiResponse<List<ProblemSolvedResponseDto>> getUserSolvedProblemList(
		@Parameter(description = "유저 깃허브 아이디", required = true) @PathVariable() String githubId,
		@Parameter(description = "루틴", required = true) @PathVariable() Routine routine,
		@Parameter(description = "조회할 시즌 (선택, 기본값: 모든 시즌)") @RequestParam(required = false) Integer season
	) {
		return CustomApiResponse.onSuccess(problemFacade.getSolvedProblemListByUser(githubId, season, routine));
	}
}
