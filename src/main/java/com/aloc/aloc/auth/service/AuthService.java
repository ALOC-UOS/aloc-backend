package com.aloc.aloc.auth.service;

import com.aloc.aloc.auth.dto.UserLoginRequestDto;
import com.aloc.aloc.scraper.BaekjoonRankScrapingService;
import com.aloc.aloc.user.dto.request.UserRequestDto;
import com.aloc.aloc.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final BaekjoonRankScrapingService baekjoonRankScrapingService;

  public void login(UserLoginRequestDto userLoginRequestDto) {
    System.out.println("Login request received for username: " + userLoginRequestDto.getGithubId());
  }

  @Transactional
  public void signUp(UserRequestDto userRequestDto) {
    // 이미 존재하는 유저인지 확인
    if (userRepository.existsByGithubId(userRequestDto.getGithubId())
        || userRepository.existsByBaekjoonId(userRequestDto.getBaekjoonId())) {
      throw new IllegalArgumentException("이미 존재하는 유저입니다.");
    }

    // 백준 랭킹 가져오기
    Integer rank = baekjoonRankScrapingService.extractBaekjoonRank(userRequestDto.getBaekjoonId());
    userRepository.save(userRequestDto.toEntity(rank, passwordEncoder));
  }

  @Transactional
  public void withdraw(String githubId) {
    userRepository.deleteByGithubId(githubId);
  }
}
