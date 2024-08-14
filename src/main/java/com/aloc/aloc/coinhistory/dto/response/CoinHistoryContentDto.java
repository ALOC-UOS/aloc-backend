package com.aloc.aloc.coinhistory.dto.response;


import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.aloc.aloc.coinhistory.CoinHistory;
import com.aloc.aloc.coinhistory.enums.CoinType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoinHistoryContentDto {
	private int coin;
	private CoinType coinType;
	private String date;

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

	public static List<CoinHistoryContentDto> listOf(List<CoinHistory> coinHistories) {
		return coinHistories.stream()
			.map(coinHistory -> CoinHistoryContentDto.builder()
				.coin(coinHistory.getCoin())
				.coinType(coinHistory.getCoinType())
				.date(coinHistory.getCreatedAt().format(DATE_FORMATTER))
				.build())
			.collect(Collectors.toList());
	}
}
