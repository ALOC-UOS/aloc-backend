package com.aloc.aloc.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.aloc.aloc.color.Color;
import com.aloc.aloc.color.service.ColorService;
import com.aloc.aloc.problem.service.ProblemFacade;
import com.aloc.aloc.problem.service.ProblemService;
import com.aloc.aloc.problem.service.ProblemSolvingService;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.enums.Authority;
import com.aloc.aloc.user.repository.UserRepository;

public class UserFacadeTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserSortingService userSortingService;

	@Mock
	private ProblemService problemService;

	@Mock
	private ProblemFacade problemFacade;

	@Mock
	private ProblemSolvingService problemSolvingService;

	@Mock
	private ColorService colorService;

	@InjectMocks
	private UserFacade userFacade;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void getUsers_ShouldReturnListOfUserDetailResponseDto() {
		// Given
		Authority authorityUser = Authority.ROLE_USER;
		Authority authorityAdmin = Authority.ROLE_ADMIN;
		List<Authority> authorities = Arrays.asList(authorityUser, authorityAdmin);

		User user1 = new User(
			"user1",
			"baekjoon1",
			"github1",
			"20210001",
			"password",
			"adminDiscord",
			1,
			"adminNotion",
			"1"
		);
		user1.setId(1L);

		User user2 = new User(
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
		user2.setId(2L);
		List<User> mockUsers = Arrays.asList(user1, user2);

		when(userRepository.findAllByAuthorityIn(authorities)).thenReturn(mockUsers);
		when(userSortingService.sortUserList(mockUsers)).thenReturn(mockUsers);
		when(problemSolvingService.getSolvedCount(anyLong())).thenReturn(10);
		when(problemFacade.getTodayProblemSolved(anyLong(), any(Course.class))).thenReturn(true);
		when(problemFacade.getThisWeekSolvedCount(any())).thenReturn(Arrays.asList(8, 15, 7));

		Color mockColor = Color.builder().id("White").color1("#FFFFFF").build();
		when(colorService.getColorById(anyString())).thenReturn(mockColor);
		when(problemService.getTotalProblemCount(any())).thenReturn(Arrays.asList(20, 30));

		// When
		List<UserDetailResponseDto> result = userFacade.getUsers();

		// Then
		assertEquals(2, result.size());
		assertEquals("user1", result.get(0).getUsername());
		assertEquals("github1", result.get(0).getGithubId());
		assertEquals("baekjoon1", result.get(0).getBaekjoonId());
		assertEquals(10, result.get(0).getSolvedCount());
		assertEquals(7, result.get(0).getThisWeekUnsolvedCount());

		UserDetailResponseDto userDetailResponseDto1 = result.get(0);
		assertEquals("user1", userDetailResponseDto1.getUsername());

		UserDetailResponseDto userDetailResponseDto2 = result.get(1);
		assertEquals("user", userDetailResponseDto2.getUsername());

		verify(userRepository, times(1)).findAllByAuthorityIn(authorities);
		verify(userSortingService, times(1)).sortUserList(mockUsers);
		verify(problemSolvingService, times(2)).getSolvedCount(anyLong());
		verify(problemFacade, times(2)).getTodayProblemSolved(anyLong(), any());
		verify(colorService, times(2)).getColorById(anyString());
		verify(problemService, times(2)).getTotalProblemCount(any());
	}
}
