package com.aloc.aloc.problem.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.service.ProblemFacade;
import com.aloc.aloc.problem.service.ProblemService;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.user.dto.response.SolvedUserResponseDto;



@WebMvcTest(ProblemController.class)
public class ProblemControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProblemService problemService;

	@MockBean
	private ProblemFacade problemFacade;

	@Test
	@WithMockUser
	@DisplayName("해당 문제를 푼 사용자 목록을 조회합니다.")
	void getSolvedUserList_shouldReturnListOfSolvedUsers() throws Exception {
		// Given
		Long problemId = 1L;
		List<SolvedUserResponseDto> solvedUsers = Arrays.asList(
			SolvedUserResponseDto.builder().username("user1").githubId("github1").build(),
			SolvedUserResponseDto.builder().username("user2").githubId("github2").build()
		);
		when(problemFacade.getSolvedUserListByProblemId(problemId)).thenReturn(solvedUsers);

		// When & Then
		mockMvc.perform(get("/api2/problem/solved-user/{problemId}", problemId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("COMMON200"))
			.andExpect(jsonPath("$.message").value("성공입니다."))
			.andExpect(jsonPath("$.result").isArray())
			.andExpect(jsonPath("$.result[0].username").value("user1"))
			.andExpect(jsonPath("$.result[1].username").value("user2"));
	}

	@Test
	@WithMockUser
	@DisplayName("FULL 코스의 오늘의 문제를 조회합니다.")
	void getTodayProblem_withFullCourse_shouldReturnTodayProblem() throws Exception {
		// Given
		when(problemService.getTodayProblemDto(Course.FULL)).thenReturn(ProblemResponseDto.builder()
			.id(1L)
			.title("Today's Problem")
			.difficulty(3)
			.build());

		// When & Then
		mockMvc.perform(get("/api2/problem/today/{course}", Course.FULL)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("COMMON200"))
			.andExpect(jsonPath("$.message").value("성공입니다."))
			.andExpect(jsonPath("$.result.id").value(1))
			.andExpect(jsonPath("$.result.title").value("Today's Problem"))
			.andExpect(jsonPath("$.result.tags").isEmpty())
			.andExpect(jsonPath("$.result.difficulty").value(3));

		// 메소드 호출 확인
		verify(problemService).getTodayProblemDto(Course.FULL);
	}

	@Test
	@WithMockUser
	@DisplayName("HALF 코스의 오늘의 문제를 조회합니다.")
	void getTodayProblem_withHalfCourse_shouldReturnTodayProblem() throws Exception {
		// Given
		when(problemService.getTodayProblemDto(Course.HALF)).thenReturn(ProblemResponseDto.builder()
			.id(1L)
			.title("Today's Problem")
			.difficulty(3)
			.build());

		// When & Then
		mockMvc.perform(get("/api2/problem/today/{course}", Course.HALF)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("COMMON200"))
			.andExpect(jsonPath("$.message").value("성공입니다."))
			.andExpect(jsonPath("$.result.id").value(1))
			.andExpect(jsonPath("$.result.title").value("Today's Problem"))
			.andExpect(jsonPath("$.result.tags").isEmpty())
			.andExpect(jsonPath("$.result.difficulty").value(3));

		// 메소드 호출 확인
		verify(problemService).getTodayProblemDto(Course.HALF);
	}

	@Test
	@WithMockUser
	@DisplayName("FULL 코스의 오늘의 문제가 없을 때 오류를 반환합니다.")
	void getTodayProblem_withFullCourse_NotFound_shouldReturnException() throws Exception {
		// Given
		Course course = Course.FULL;
		when(problemService.getTodayProblemDto(course))
			.thenThrow(new IllegalArgumentException("오늘의 문제가 없습니다."));

		// When & Then
		mockMvc.perform(get("/api2/problem/today/{course}", course)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.code").value("COMMON400"))
			.andExpect(jsonPath("$.message").value("오늘의 문제가 없습니다."))
			.andExpect(jsonPath("$.result").value("잘못된 요청입니다."));


		verify(problemService).getTodayProblemDto(course);
	}

	@Test
	@WithMockUser
	@DisplayName("HALF 코스의 오늘의 문제가 없을 때 오류를 반환합니다.")
	void getTodayProblem_withHalfCourse_NotFound_shouldReturnException() throws Exception {
		// Given
		Course course = Course.HALF;
		when(problemService.getTodayProblemDto(course))
			.thenThrow(new IllegalArgumentException("오늘의 문제가 없습니다."));

		// When & Then
		mockMvc.perform(get("/api2/problem/today/{course}", course)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.code").value("COMMON400"))
			.andExpect(jsonPath("$.message").value("오늘의 문제가 없습니다."))
			.andExpect(jsonPath("$.result").value("잘못된 요청입니다."));


		verify(problemService).getTodayProblemDto(course);
	}

	@Test
	@WithMockUser
	@DisplayName("잘못된 코스로 오늘의 문제를 조회할 때 오류를 반환합니다.")
	void getTodayProblem_withInvalidCourse_shouldReturnBadRequest() throws Exception {
		// Given
		String course = "Invalid";

		// When & Then
		mockMvc.perform(get("/api2/problem/today/{course}", course)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.code").value("COMMON400"));
	}

	@Test
	@WithMockUser
	@DisplayName("알고리즘 Id와 시즌으로 공개된 문제 불러오기")
	void getProblemsByAlgorithmIdAndSeason() throws Exception {
		// given
		int season = 1;
		int algorithmId = 1;

		List<ProblemResponseDto> problems = Arrays.asList(
			new ProblemResponseDto(1L, "Problem 1", null, 3, 100),
			new ProblemResponseDto(2L, "Problem 2", null, 4, 50)
		);
		when(problemService.getVisibleProblemsDtoByAlgorithmId(season, algorithmId, Course.FULL)).thenReturn(problems);

		// when & then
		mockMvc.perform(get("/api2/problem/season/{season}/algorithmId/{algorithmId}/course/FULL", season, algorithmId)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("COMMON200"))
			.andExpect(jsonPath("$.result").isArray())
			.andExpect(jsonPath("$.result[0].id").value(1))
			.andExpect(jsonPath("$.result[0].title").value("Problem 1"))
			.andExpect(jsonPath("$.result[0].tags").isEmpty())
			.andExpect(jsonPath("$.result[0].difficulty").value(3))
			.andExpect(jsonPath("$.result[0].solvingCount").value(100))
			.andExpect(jsonPath("$.result[1].id").value(2))
			.andExpect(jsonPath("$.result[1].title").value("Problem 2"))
			.andExpect(jsonPath("$.result[1].tags").isEmpty())
			.andExpect(jsonPath("$.result[1].difficulty").value(4))
			.andExpect(jsonPath("$.result[1].solvingCount").value(50));
	}
}
