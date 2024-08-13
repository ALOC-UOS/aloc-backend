package com.aloc.aloc.coinhistory.service;

import org.springframework.stereotype.Service;

import com.aloc.aloc.coinhistory.CoinHistory;
import com.aloc.aloc.coinhistory.enums.CoinType;
import com.aloc.aloc.coinhistory.repository.CoinHistoryRepository;
import com.aloc.aloc.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoinHistoryService {
	private final CoinHistoryRepository coinHistoryRepository;
	public void addCoinHistory(User user, int coin, CoinType coinType, String description) {
		CoinHistory coinHistory = CoinHistory.builder()
			.coin(coin)
			.coinType(coinType)
			.user(user)
			.description(description)
			.build();
		coinHistoryRepository.save(coinHistory);
	}
}
