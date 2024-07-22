package com.aloc.aloc.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aloc.aloc.history.service.HistoryService;
import com.aloc.aloc.scraper.BaekjoonRankScrapingService;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.enums.Authority;
import com.aloc.aloc.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final HistoryService historyService;
	private final BaekjoonRankScrapingService baekjoonRankScrapingService;

	public void checkAdmin(String githubId) {
		Optional<User> userOptional = userRepository.findByGithubId(githubId);
		if (userOptional.isEmpty()) {
			throw new IllegalStateException("로그인 정보가 없습니다.");
		}
		User user = userOptional.get();
		if (!Authority.ROLE_ADMIN.equals(user.getAuthority())) {
			throw new IllegalStateException("관리자만 가능합니다.");
		}
	}

	public void checkUserRank(User user) {
		Integer rank = baekjoonRankScrapingService.extractBaekjoonRank(user.getBaekjoonId());
		if (!user.getRank().equals(rank)) {
			updateUserRank(user, rank);
		}
	}

	private void updateUserRank(User user, Integer rank) {
		user.setRank(rank);
		userRepository.save(user);
		historyService.addHistory(user, "changeRank", rank);
	}

	public List<User> getActiveUsers() {
		List<Authority> authorities = List.of(Authority.ROLE_USER, Authority.ROLE_ADMIN);
		return userRepository.findAllByAuthorityIn(authorities);
	}
}
