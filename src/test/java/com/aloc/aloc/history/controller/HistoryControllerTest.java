package com.aloc.aloc.history.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
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

import com.aloc.aloc.history.dto.response.HistoryContentDto;
import com.aloc.aloc.history.dto.response.HistoryResponseDto;
import com.aloc.aloc.history.service.HistoryService;

@WebMvcTest(HistoryController.class)
class HistoryControllerTest {

	@MockBean
	private JpaMetamodelMappingContext jpaMetamodelMappingContext;

	@MockBean
	private HistoryService historyService;

	@Autowired
	private MockMvc mockMvc;

	private List<HistoryResponseDto> historyResponseDtos;

	@BeforeEach
	void setUp() {
		historyResponseDtos = new ArrayList<>();
		List<HistoryContentDto> historyContentDtos1 = new ArrayList<>();
		List<HistoryContentDto> historyContentDtos2 = new ArrayList<>();

		HistoryContentDto content1 = HistoryContentDto.builder().icon("plusMember").name("홍길동").rank(null).build();
		HistoryContentDto content2 = HistoryContentDto.builder().icon("plusMember").name("김장군").rank(null).build();
		HistoryContentDto content3 = HistoryContentDto.builder().icon("changeRank").name("홍길동").rank(33).build();
		HistoryContentDto content4 = HistoryContentDto.builder().icon("changeRank").name("홍길동").rank(32).build();
		HistoryContentDto content5 = HistoryContentDto.builder().icon("plusMember").name("박돌쇠").rank(null).build();
		HistoryContentDto content6 = HistoryContentDto.builder().icon("changeRank").name("홍길동").rank(31).build();

		historyContentDtos1.add(content1);
		historyContentDtos1.add(content2);
		historyContentDtos1.add(content3);
		historyContentDtos2.add(content4);
		historyContentDtos2.add(content5);
		historyContentDtos2.add(content6);

		historyResponseDtos.add(new HistoryResponseDto("2024년 01월 02일 화요일", historyContentDtos1));
		historyResponseDtos.add(new HistoryResponseDto("2024년 01월 01일 월요일", historyContentDtos2));
	}

	@Test
	@WithMockUser
	@DisplayName("모든 히스토리 목록 조회 성공")
	void testGetHistories() throws Exception {
		when(historyService.getHistories()).thenReturn(historyResponseDtos);

		mockMvc.perform(get("/api2/history").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("COMMON200"))
			.andExpect(jsonPath("$.result[0].date").value("2024년 01월 02일 화요일"))
			.andExpect(jsonPath("$.result[0].contents[0].icon").value("plusMember"))
			.andExpect(jsonPath("$.result[0].contents[0].name").value("홍길동"))
			.andExpect(jsonPath("$.result[0].contents[0].rank").isEmpty());

	}
}
