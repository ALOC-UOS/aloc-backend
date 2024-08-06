package com.aloc.aloc.auth.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.aloc.aloc.scraper.BaekjoonRankScrapingService;
import com.aloc.aloc.scraper.GithubProfileScrapingService;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.dto.request.UserRequestDto;
import com.aloc.aloc.user.repository.UserRepository;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class AuthServiceTest {

	@Autowired
	private AuthService authService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private BCryptPasswordEncoder passwordEncoder;

	@MockBean
	private BaekjoonRankScrapingService baekjoonRankScrapingService;

	@MockBean
	private GithubProfileScrapingService githubProfileScrapingService;

	private UserRequestDto createUserRequestDto() {
		UserRequestDto dto = new UserRequestDto();
		dto.setUsername("홍길동");
		dto.setPassword("1234");
		dto.setGithubId("github");
		dto.setBaekjoonId("baekjoon");
		dto.setStudentId("2021920000");
		dto.setDiscordId("discord");
		dto.setNotionEmail("notion@uos.ac.kr");
		return dto;
	}

	@Test
	@DisplayName("회원가입 서비스 성공 테스트")
	public void testSignUp_Success() {
		// given
		UserRequestDto userRequestDto = createUserRequestDto();

		when(userRepository.existsByGithubId(userRequestDto.getGithubId())).thenReturn(false);
		when(userRepository.existsByBaekjoonId(userRequestDto.getBaekjoonId())).thenReturn(false);
		when(passwordEncoder.encode(userRequestDto.getPassword())).thenReturn("encodedPassword");
		when(baekjoonRankScrapingService.extractBaekjoonRank(userRequestDto.getBaekjoonId())).thenReturn(15);
		when(githubProfileScrapingService.extractProfileNumber(userRequestDto.getGithubId())).thenReturn(
			Optional.of("20210001"));

		// when
		authService.signUp(userRequestDto);

		// then
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	@DisplayName("회원가입 실패 - GithubId 중복")
	public void testSignUp_GithubIdAlreadyExists() {
		// given
		UserRequestDto userRequestDto = createUserRequestDto();

		when(userRepository.existsByGithubId(userRequestDto.getGithubId())).thenReturn(true);

		// when & then
		assertThrows(IllegalArgumentException.class, () -> authService.signUp(userRequestDto));

		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	@DisplayName("회원가입 실패 - Baekjoon 아이디 중복")
	public void testSignUp_BaekjoonIdAlreadyExists() {
		// given
		UserRequestDto userRequestDto = createUserRequestDto();

		when(userRepository.existsByGithubId(userRequestDto.getGithubId())).thenReturn(false);
		when(userRepository.existsByBaekjoonId(userRequestDto.getBaekjoonId())).thenReturn(true);

		// when & then
		assertThrows(IllegalArgumentException.class, () -> authService.signUp(userRequestDto));

		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	@DisplayName("회원가입 실패 - GitHub 프로필 없음")
	public void testSignUp_FailToGetGithubProfile() {
		// given
		UserRequestDto userRequestDto = createUserRequestDto();

		// when
		when(userRepository.existsByGithubId(userRequestDto.getGithubId())).thenReturn(false);
		when(userRepository.existsByBaekjoonId(userRequestDto.getBaekjoonId())).thenReturn(false);
		when(passwordEncoder.encode(userRequestDto.getPassword())).thenReturn("encodedPassword");
		when(baekjoonRankScrapingService.extractBaekjoonRank(
			userRequestDto.getBaekjoonId())).thenReturn(15);
		when(githubProfileScrapingService.extractProfileNumber(userRequestDto.getGithubId()))
			.thenReturn(Optional.empty());

		// then
		assertThrows(IllegalArgumentException.class, () -> authService.signUp(userRequestDto));
		verify(userRepository, never()).save(any(User.class));
		verify(githubProfileScrapingService).extractProfileNumber(userRequestDto.getGithubId());
	}

	@Test
	@DisplayName("회원가입 실패 - Baekjoon 랭크 추출 실패")
	public void testSignUp_BaekjoonRankExtractionFails() {
		UserRequestDto userRequestDto = createUserRequestDto();

		when(userRepository.existsByGithubId(userRequestDto.getGithubId())).thenReturn(false);
		when(userRepository.existsByBaekjoonId(userRequestDto.getBaekjoonId())).thenReturn(false);
		when(githubProfileScrapingService.extractProfileNumber(userRequestDto.getGithubId())).thenReturn(
			Optional.of("20210001"));
		when(baekjoonRankScrapingService.extractBaekjoonRank(userRequestDto.getBaekjoonId()))
			.thenThrow(new RuntimeException("Baekjoon rank extraction failed"));

		assertThrows(RuntimeException.class, () -> authService.signUp(userRequestDto));
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	@DisplayName("회원 탈퇴 성공 테스트")
	public void testWithdraw() {
		// given
		String githubId = "github";
		when(userRepository.existsByGithubId(githubId)).thenReturn(true);

		// when
		authService.withdraw(githubId);

		// then
		verify(userRepository, times(1)).deleteByGithubId(githubId);
	}
}
