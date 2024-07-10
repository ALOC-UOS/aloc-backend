package com.aloc.aloc.history.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import com.aloc.aloc.history.History;
import com.aloc.aloc.history.dto.response.HistoryResponseDto;
import com.aloc.aloc.history.repository.HistoryRepository;

@ExtendWith(MockitoExtension.class)
@Transactional
class HistoryServiceTest {

	@Mock
	private HistoryRepository historyRepository;

	@InjectMocks
	private HistoryService historyService;

	private List<History> histories;

	@BeforeEach
	void setUp() {
		histories = new ArrayList<>();
		History history1 = new History(1L, null, "홍길동", "plusMember", null);
		History history2 = new History(2L, null, "홍길동", "changeRank", 33);
		History history3 = new History(3L, null, "홍길동", "changeRank", 32);
		history1.setCreatedAt(LocalDateTime.now().minusDays(1));
		history2.setCreatedAt(LocalDateTime.now().minusDays(1));
		history3.setCreatedAt(LocalDateTime.now());

		histories.add(history1);
		histories.add(history2);
		histories.add(history3);
	}

	@Test
	@DisplayName("모든 히스토리 목록 그룹화 하여 최신순으로 정렬")
	void getHistories() {
		when(historyRepository.findByOrderByCreatedAtDesc())
			.thenReturn(histories);

		List<HistoryResponseDto> result = historyService.getHistories();

		assertThat(result).hasSize(2);
		HistoryResponseDto history1 = result.get(0);
		assertThat(history1).isNotNull();
		assertThat(history1.getDate()).isNotNull();
		assertThat(history1.getContents().get(0).getRank()).isEqualTo(32);

		HistoryResponseDto history2 = result.get(1);
		assertThat(history2).isNotNull();
		assertThat(history2.getDate()).isNotNull();
		assertThat(history2.getContents().get(0).getRank()).isEqualTo(33);
	}
}
