package com.aloc.aloc.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.service.AlgorithmService;
import com.aloc.aloc.color.Color;
import com.aloc.aloc.color.service.ColorService;
import com.aloc.aloc.problem.service.ProblemFacade;
import com.aloc.aloc.problem.service.ProblemService;
import com.aloc.aloc.problem.service.ProblemSolvingService;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.repository.UserRepository;
@ExtendWith(MockitoExtension.class)
class UserFacadeTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private UserSortingService userSortingService;
	@Mock
	private AlgorithmService algorithmService;
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
	void setUp() {
		ReflectionTestUtils.setField(userFacade, "season", 1);
	}

	@Test
	void getUsers_ShouldReturnListOfUserDetailResponseDto() {
		// Given
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

		List<User> users = Arrays.asList(user1, user2);
		Algorithm mockAlgorithm = mock(Algorithm.class);
		when(mockAlgorithm.getAlgorithmId()).thenReturn(1);
		when(userRepository.findAllByAuthorityIn(anyList())).thenReturn(users);
		when(userSortingService.sortUserList(users)).thenReturn(users);
		when(problemSolvingService.getSolvedCount(anyLong())).thenReturn(10);
		when(problemService.getTotalProblemCount(any(Course.class))).thenReturn(20);
		when(problemFacade.getTodayProblemSolved(anyLong(), any(Course.class))).thenReturn(Boolean.TRUE);
		when(algorithmService.getAlgorithmBySeason(anyInt())).thenReturn(Optional.of(mockAlgorithm));
		when(problemFacade.getThisWeekSolvedCount(anyLong(), anyInt(), anyInt(), any(Course.class)))
			.thenReturn(Arrays.asList(8, 15, 7));

		Color color = Color.builder()
			.id("Red")
			.category("PRIMARY")
			.color1("#FF0000")
			.color2("#CC0000")
			.build();
		when(colorService.getColorById(anyString())).thenReturn(color);

		// When
		List<UserDetailResponseDto> result = userFacade.getUsers();

		// Then
		assertNotNull(result);
		assertEquals(2, result.size());

		UserDetailResponseDto firstUser = result.get(0);
		assertEquals("user1", firstUser.getUsername());
		assertEquals("github1", firstUser.getGithubId());
		assertEquals("baekjoon1", firstUser.getBaekjoonId());
		assertEquals("Blue", firstUser.getProfileColor());
		assertEquals(10, firstUser.getSolved());
		assertEquals(10, firstUser.getUnsolved());  // 20 total - 10 solved
		assertEquals(Boolean.TRUE, firstUser.getTodayUnsolved());
		assertEquals(7, firstUser.getThisWeekUnsolved());
		assertEquals("PRIMARY", firstUser.getColorCategory());
		assertEquals("#FF0000", firstUser.getColor1());
		assertEquals("#CC0000", firstUser.getColor2());

		verify(userRepository).findAllByAuthorityIn(anyList());
		verify(userSortingService).sortUserList(users);
		verify(problemSolvingService, times(2)).getSolvedCount(anyLong());
		verify(problemService, times(2)).getTotalProblemCount(any(Course.class));
		verify(problemFacade, times(2)).getTodayProblemSolved(anyLong(), any(Course.class));
		verify(algorithmService, times(2)).getAlgorithmBySeason(anyInt());
		verify(problemFacade, times(2)).getThisWeekSolvedCount(anyLong(), anyInt(), anyInt(), any(Course.class));
		verify(colorService, times(2)).getColorById(anyString());
	}
}
