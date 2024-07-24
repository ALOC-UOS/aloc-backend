package com.aloc.aloc.problem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserProblem;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.dto.response.SolvedUserResponseDto;

@ExtendWith(MockitoExtension.class)
public class ProblemFacadeTest {
	@Spy
	@InjectMocks
	private ProblemFacade problemFacade;

	@Mock
	private ProblemService problemService;

	@Mock
	private ProblemSolvingService problemSolvingService;

	@Mock
	private ProblemMapper problemMapper;

	private List<UserProblem> user1SolvedProblems;
	private List<SolvedUserResponseDto> solvedUsers;
	private List<UserProblem> solvedProblems;
	private User user1;

	@BeforeEach
	void setUp() {
		// Set up Users
		user1 = new User(
			"user1",
			"baekjoon1",
			"github1",
			"20210001",
			"password",
			"discord",
			15,
			"notion",
			"11550",
			Course.FULL
		);
		user1.setId(1L);
		User user2 = new User(
			"user2",
			"baekjoon2",
			"github2",
			"20210002",
			"password",
			"discord",
			15,
			"11551",
			"notion",
			Course.FULL
		);
		user2.setId(2L);

		// Set up Algorithms
		Algorithm algorithm1 = new Algorithm(1, 1, "Algorithm 1", 2, null);
		Algorithm algorithm2 = new Algorithm(2, 2, "Algorithm 2", 2, null);

		// Set up Problems
		Problem problem1 = new Problem("Problem 1", 3, algorithm1, null, null);
		Problem problem2 = new Problem("Problem 2", 4, algorithm2, null, null);
		problem1.setId(1L);
		problem2.setId(2L);
		List<Problem> problems = Arrays.asList(problem1, problem2);

		// Set up ProblemResponseDtos
		List<ProblemResponseDto> problemResponseDtos = Arrays.asList(
			ProblemResponseDto.builder().id(1L).title("Problem 1").build(),
			ProblemResponseDto.builder().id(2L).title("Problem 2").build()
		);

		// Set up SolvedUserResponseDtos
		solvedUsers = Arrays.asList(
			SolvedUserResponseDto.builder()
				.username("user1").githubId("github1").baekjoonId("baekjoon1")
				.profileColor("blue").studentId("20210001").profileNumber("1")
				.rank(10).coin(100).solvedAt("11:30:00").build(),
			SolvedUserResponseDto.builder()
				.username("user2").githubId("github2").baekjoonId("baekjoon2")
				.profileColor("red").studentId("20210002").profileNumber("2")
				.rank(20).coin(200).solvedAt("12:45:00").build()
		);

		// Set up SolvedProblem
		solvedProblems = Arrays.asList(
			UserProblem.builder().user(user1).problem(problem1).season(2).isSolved(true).build(),
			UserProblem.builder().user(user2).problem(problem1).season(2).isSolved(true).build()
		);

		user1SolvedProblems = Arrays.asList(
			UserProblem.builder().user(user1).problem(problem1).season(2).isSolved(true).build(),
			UserProblem.builder().user(user1).problem(problem2).season(2).isSolved(true).build()
		);
	}

	@Test
	@DisplayName("문제 푼 유저 목록 조회 테스트")
	void getSolvedUserListByProblemId_shouldReturnListOfSolvedUserResponseDto() {
		// Given
		Long problemId = 1L;
		List<SolvedUserResponseDto> expectedUsers = solvedUsers;
		when(problemSolvingService.getSolvedUserListByProblemId(problemId)).thenReturn(solvedProblems);

		// problemMapper.mapToSolvedUserResponseDto() 모킹 추가
		for (int i = 0; i < solvedProblems.size(); i++) {
			when(problemMapper.mapToSolvedUserResponseDto(solvedProblems.get(i).getUser(), solvedProblems.get(i)))
				.thenReturn(expectedUsers.get(i));
		}

		// When
		List<SolvedUserResponseDto> actualUsers = problemFacade.getSolvedUserListByProblemId(problemId);

		// Then
		assertEquals(expectedUsers, actualUsers);
	}

	@Test
	@DisplayName("유저가 푼 문제 목록 조회 성공 테스트")
	void getSolvedProblemListByUser_shouldReturnListOfSolvedProblem() {
		// Given=
		when(problemService.findUser(user1.getGithubId())).thenReturn(user1); // 추가된 부분
		when(problemSolvingService.getUserProblemList(user1.getId(), 2, true, null))
			.thenReturn(user1SolvedProblems); // 추가된 부분
		when(problemMapper.mapSolvedProblemToDtoList(user1SolvedProblems)).thenReturn(
			Arrays.asList(
				ProblemSolvedResponseDto.builder()
					.problemId(1L)
					.problemTitle("Problem 1")
					.isSolved(true)
					.problemDifficulty(3)
					.build(),
				ProblemSolvedResponseDto.builder()
					.problemId(2L)
					.problemTitle("Problem 2")
					.isSolved(true)
					.problemDifficulty(4)
					.build()
			));

		// When
		List<ProblemSolvedResponseDto> result = problemFacade.getSolvedProblemListByUser(user1.getGithubId(), 2, null);

		// Then
		assertEquals(2, result.size());
		verify(problemService).findUser(user1.getGithubId()); // 추가된 부분
	}

	@Test
	@DisplayName("유저가 푼 문제 목록 조회 실패 테스트")
	void checkSolved_LoadUserProblemRecordThrowsException_PropagatesException() {
		// Arrange
		String username = "testUser";
		when(problemService.findUser(username)).thenReturn(user1);
		doThrow(new RuntimeException("Error loading problem record")).when(problemService).loadUserProblemRecord(user1);

		// Act & Assert
		assertThrows(RuntimeException.class, () -> problemFacade.checkSolved(username));
		verify(problemService).findUser(username);
		verify(problemService).loadUserProblemRecord(user1);
	}
}
