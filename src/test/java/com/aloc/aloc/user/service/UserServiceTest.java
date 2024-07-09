package com.aloc.aloc.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aloc.aloc.user.User;
import com.aloc.aloc.user.dto.response.UserResponseDto;
import com.aloc.aloc.user.enums.Authority;
import com.aloc.aloc.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	@Mock
	private UserRepository userRepository;

	@Mock
	private UserSortingService userSortingService;

	@InjectMocks
	private UserService userService;

	private User adminUser;
	private User regularUser;
	private User newUser;

	@BeforeEach
	void setUp() {
		adminUser = new User(
			"admin",
			"adminBaekjoon",
			"adminGithub",
			"20210001",
			"password",
			"adminDiscord",
			1,
			"adminNotion",
			"1"
		);
		adminUser.setAuthority(Authority.ROLE_ADMIN);

		regularUser = new User(
			"user",
			"userBaekjoon",
			"userGithub",
			"20210002",
			"password",
			"userDiscord",
			2,
			"userNotion",
			"2"
		);
		regularUser.setAuthority(Authority.ROLE_USER);

		newUser = new User(
			"newUser",
			"newBaekjoon",
			"newGithub",
			"20210003",
			"password",
			"newDiscord",
			3,
			"newNotion",
			"3"
		);
		newUser.setAuthority(Authority.ROLE_GUEST);
	}

	@Test
	@DisplayName("스터디 멤버 목록 조회 테스트")
	void getUsers_shouldReturnOnlyRegularUsers() {
		// Given
		List<Authority> authorities = Arrays.asList(Authority.ROLE_USER, Authority.ROLE_ADMIN);
		when(userRepository.findAllByAuthorityIn(authorities)).thenReturn(Arrays.asList(regularUser));
		when(userSortingService.sortUserList(Arrays.asList(regularUser))).thenReturn(Arrays.asList(regularUser));

		// When
		List<UserResponseDto> result = userService.getUsers();

		// Then
		assertEquals(1, result.size());
		assertEquals(regularUser.getUsername(), result.get(0).getUsername());

		verify(userRepository).findAllByAuthorityIn(authorities);
	}

	@Test
	@DisplayName("관리자가 스터디 멤버 추가 테스트")
	void addUser_withAdminAndValidNewUser_shouldSucceed() {
		// Given
		when(userRepository.findByGithubId("adminGithub")).thenReturn(Optional.of(adminUser));
		when(userRepository.findByGithubId("newGithub")).thenReturn(Optional.of(newUser));

		// When
		String result = userService.addUser("adminGithub", "newGithub");

		// Then
		assertEquals("스터디 멤버로 등록되었습니다.", result);
		assertEquals(Authority.ROLE_USER, newUser.getAuthority());
		verify(userRepository).findByGithubId("adminGithub");
		verify(userRepository).findByGithubId("newGithub");
	}

	@Test
	@DisplayName("관리자가 아닌 멤버가 스터디 멤버 추가시 예외 처리 테스트")
	void addUser_withNonAdminUser_shouldThrowException() {
		// Given
		when(userRepository.findByGithubId("userGithub")).thenReturn(Optional.of(regularUser));

		// When & Then
		assertThrows(IllegalStateException.class, () -> userService.addUser("userGithub", "newGithub"));
		verify(userRepository).findByGithubId("userGithub");
	}

	@Test
	@DisplayName("스터디 멤버 추가 시 이미 등록된 사용자일 경우 예외 처리 테스트")
	void addUser_withNonExistentUser_shouldThrowException() {
		// Given
		when(userRepository.findByGithubId("adminGithub")).thenReturn(Optional.of(adminUser));
		when(userRepository.findByGithubId("nonExistentGithub")).thenReturn(Optional.empty());

		// When & Then
		assertThrows(IllegalArgumentException.class, () -> userService.addUser("adminGithub", "nonExistentGithub"));
		verify(userRepository).findByGithubId("adminGithub");
		verify(userRepository).findByGithubId("nonExistentGithub");
	}

	@Test
	@DisplayName("이미 등록된 사용자일 경우 예외 처리 테스트")
	void addUser_withAlreadyRegisteredUser_shouldThrowException() {
		// Given
		when(userRepository.findByGithubId("adminGithub")).thenReturn(Optional.of(adminUser));
		when(userRepository.findByGithubId("userGithub")).thenReturn(Optional.of(regularUser));

		// When & Then
		assertThrows(IllegalArgumentException.class, () -> userService.addUser("adminGithub", "userGithub"));
		verify(userRepository).findByGithubId("adminGithub");
		verify(userRepository).findByGithubId("userGithub");
	}
}
