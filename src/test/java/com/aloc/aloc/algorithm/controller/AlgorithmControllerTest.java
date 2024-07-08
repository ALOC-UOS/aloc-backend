package com.aloc.aloc.algorithm.controller;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.aloc.aloc.algorithm.dto.response.AlgorithmDto;
import com.aloc.aloc.algorithm.dto.response.AlgorithmResponseDto;
import com.aloc.aloc.algorithm.service.AlgorithmService;

@WebMvcTest(AlgorithmController.class)
class AlgorithmControllerTest {

	@MockBean
	private JpaMetamodelMappingContext jpaMetamodelMappingContext;

	@MockBean
	private AlgorithmService algorithmService;

	@Autowired
	private MockMvc mockMvc;

	private List<AlgorithmResponseDto> algorithmResponseDtos;

	@BeforeEach
	void setUp() {
		algorithmResponseDtos = new ArrayList<>();
		for (int season = 3; season >= 1; season--) {
			algorithmResponseDtos.add(createAlgorithmResponseDto(season));
		}
	}

	private AlgorithmResponseDto createAlgorithmResponseDto(int season) {
		List<AlgorithmDto> algorithmDtos = Arrays.asList(
			createAlgorithmDto(3, season),
			createAlgorithmDto(2, season),
			createAlgorithmDto(1, season)
		);
		return new AlgorithmResponseDto(season, algorithmDtos);
	}

	private AlgorithmDto createAlgorithmDto(int id, int season) {
		int algorithmId = 3 * season - (3 - id);
		return new AlgorithmDto(id, algorithmId, "알고리즘 " + algorithmId, false);
	}

	@Test
	@WithMockUser
	@DisplayName("모든 알고리즘 목록 조회 성공")
	void testGetAlgorithms() throws Exception {
		when(algorithmService.getAlgorithms()).thenReturn(algorithmResponseDtos);

		mockMvc.perform(get("/api2/algorithm").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("COMMON200"))
			.andExpect(jsonPath("$.message").value("성공입니다."))
			.andExpect(jsonPath("$.result").isArray())
			.andExpect(jsonPath("$.result[0].season").value(3))
			.andExpect(jsonPath("$.result[0].algorithms[0].week").value(3))
			.andExpect(jsonPath("$.result[0].algorithms[0].algorithmId").value(9))
			.andExpect(jsonPath("$.result[0].algorithms[0].name").value("알고리즘 9"))
			.andExpect(jsonPath("$.result[0].algorithms[0].hidden").value(false));
	}

	@Test
	@WithMockUser
	@DisplayName("시즌을 기준으로 알고리즘 목록 조회 성공")
	void testGetAlgorithmsBySeason() throws Exception {
		when(algorithmService.getAlgorithmsBySeason(1)).thenReturn(algorithmResponseDtos.get(2));

		mockMvc.perform(get("/api2/algorithm/{season}", 1).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("COMMON200"))
			.andExpect(jsonPath("$.message").value("성공입니다."))
			.andExpect(jsonPath("$.result.season").value(1))
			.andExpect(jsonPath("$.result.algorithms[0].week").value(3))
			.andExpect(jsonPath("$.result.algorithms[0].algorithmId").value(3))
			.andExpect(jsonPath("$.result.algorithms[0].name").value("알고리즘 3"))
			.andExpect(jsonPath("$.result.algorithms[0].hidden").value(false));
	}
}
