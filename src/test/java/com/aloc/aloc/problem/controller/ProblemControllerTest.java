package com.aloc.aloc.problem.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.service.ProblemService;
import com.aloc.aloc.user.dto.response.SolvedUserResponseDto;



@WebMvcTest(ProblemController.class)
public class ProblemControllerTest {
	@MockBean
	private JpaMetamodelMappingContext jpaMetamodelMappingContext;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProblemService problemService;

	@Test
	@WithMockUser
	void getProblems_shouldReturnListOfProblems() throws Exception {
		// Given
		List<ProblemResponseDto> problems = Arrays.asList(
			new ProblemResponseDto(1L, "Problem 1", null, 3, 100),
			new ProblemResponseDto(2L, "Problem 2", null, 4, 50)
		);
		when(problemService.getProblems()).thenReturn(problems);

		// When & Then
		mockMvc.perform(get("/api2/problem")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.isSuccess").value(true))
				.andExpect(jsonPath("$.code").value("COMMON200"))
				.andExpect(jsonPath("$.message").value("성공입니다."))
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

	@Test
	@WithMockUser
	void getSolvedUserList_shouldReturnListOfSolvedUsers() throws Exception {
		// Given
		Long problemId = 1L;
		List<SolvedUserResponseDto> solvedUsers = Arrays.asList(
			SolvedUserResponseDto.builder().username("user1").githubId("github1").build(),
			SolvedUserResponseDto.builder().username("user2").githubId("github2").build()
		);
		when(problemService.getSolvedUserListByProblemId(problemId)).thenReturn(solvedUsers);

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
}
