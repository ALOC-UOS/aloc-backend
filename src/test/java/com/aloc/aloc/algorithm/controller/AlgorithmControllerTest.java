package com.aloc.aloc.algorithm.controller;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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

	@Test
	@WithMockUser
	@DisplayName("시즌을 기준으로 알고리즘 목록 조회 성공")
	void testGetAlgorithmsBySeason() throws Exception {
		int season = 1;
		List<AlgorithmResponseDto> algorithmResponseDtos = Arrays.asList(
			new AlgorithmResponseDto(3, 1, "알고리즘 1", season, false),
			new AlgorithmResponseDto(2, 2, "알고리즘 2", season, false),
			new AlgorithmResponseDto(1, 3, "알고리즘 3", season, false)
		);

		when(algorithmService.getAlgorithmsBySeason(season)).thenReturn(algorithmResponseDtos);

		mockMvc.perform(get("/api2/algorithm/{season}", season)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("COMMON200"))
			.andExpect(jsonPath("$.message").value("성공입니다."))
			.andExpect(jsonPath("$.result").isArray())
			.andExpect(jsonPath("$.result[0].id").value(3))
			.andExpect(jsonPath("$.result[0].algorithmId").value(1))
			.andExpect(jsonPath("$.result[0].name").value("알고리즘 1"))
			.andExpect(jsonPath("$.result[0].season").value(season))
			.andExpect(jsonPath("$.result[0].hidden").value(false));
	}
}
