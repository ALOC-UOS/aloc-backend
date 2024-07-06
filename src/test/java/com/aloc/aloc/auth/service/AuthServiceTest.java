package com.aloc.aloc.auth.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.aloc.aloc.scraper.BaekjoonRankScrapingService;
import com.aloc.aloc.scraper.GithubProfileScrapingService;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.dto.request.UserRequestDto;
import com.aloc.aloc.user.repository.UserRepository;


@SpringBootTest
@Transactional
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

	@Test
	@DisplayName("회원가입 서비스 성공 테스트")
	public void testSignUp_Success() {
		// given
		UserRequestDto userRequestDto = new UserRequestDto();
		userRequestDto.setUsername("홍길동");
		userRequestDto.setPassword("1234");
		userRequestDto.setGithubId("github");
		userRequestDto.setBaekjoonId("baekjoon");
		userRequestDto.setStudentId("2021920000");
		userRequestDto.setDiscordId("discord");
		userRequestDto.setNotionEmail("notion@uos.ac.kr");

		when(userRepository.existsByGithubId(userRequestDto.getGithubId())).thenReturn(false);
		when(userRepository.existsByBaekjoonId(userRequestDto.getBaekjoonId())).thenReturn(false);
		when(passwordEncoder.encode(userRequestDto.getPassword())).thenReturn("encodedPassword");
		when(baekjoonRankScrapingService.extractBaekjoonRank(userRequestDto.getBaekjoonId())).thenReturn(15);
		when(githubProfileScrapingService.extractProfileNumber(userRequestDto.getGithubId())).thenReturn("20210001");

		// when
		authService.signUp(userRequestDto);

		// then
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	@DisplayName("회원가입 서비스 실패 테스트(동일한 github 아이디 존재)")
	public void testSignUp_GihubIdAlreadyExists() {
		// given
		UserRequestDto userRequestDto = new UserRequestDto();
		userRequestDto.setUsername("홍길동");
		userRequestDto.setPassword("1234");
		userRequestDto.setGithubId("github");
		userRequestDto.setBaekjoonId("baekjoon");
		userRequestDto.setStudentId("2021920000");
		userRequestDto.setDiscordId("discord");
		userRequestDto.setNotionEmail("notion@uos.ac.kr");

		when(userRepository.existsByGithubId(userRequestDto.getGithubId())).thenReturn(true);

		// when & then
		assertThrows(IllegalArgumentException.class, () -> authService.signUp(userRequestDto));

		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	@DisplayName("회원가입 서비스 실패 테스트(동일한 baekjoon 아이디 존재)")
	public void testSignUp_BaekjoonIdAlreadyExists() {
		// given
		UserRequestDto userRequestDto = new UserRequestDto();
		userRequestDto.setUsername("홍길동");
		userRequestDto.setPassword("1234");
		userRequestDto.setGithubId("github");
		userRequestDto.setBaekjoonId("baekjoon");
		userRequestDto.setStudentId("2021920000");
		userRequestDto.setDiscordId("discord");
		userRequestDto.setNotionEmail("notion@uos.ac.kr");

		when(userRepository.existsByBaekjoonId(userRequestDto.getBaekjoonId())).thenReturn(true);

		// when & then
		assertThrows(IllegalArgumentException.class, () -> authService.signUp(userRequestDto));

		verify(userRepository, never()).save(any(User.class));
	}
}
