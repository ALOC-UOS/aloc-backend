package com.aloc.aloc.problem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.SolvedProblem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.problem.repository.SolvedProblemRepository;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.dto.response.SolvedUserResponseDto;


@ExtendWith(MockitoExtension.class)
public class ProblemFacadeTest {
	@InjectMocks
	private ProblemFacade problemFacade;

	@Mock
	private ProblemService problemService;

	@Mock
	private ProblemRepository problemRepository;

	@Mock
	private SolvedProblemRepository solvedProblemRepository;

	@Mock
	private ProblemSolvingService problemSolvingService;

	@Mock
	private ProblemMapper problemMapper;

	private List<Problem> problems;
	private List<ProblemResponseDto> problemResponseDtos;
	private List<SolvedUserResponseDto> solvedUsers;
	private List<SolvedProblem> solvedProblems;

	@BeforeEach
	void setUp() {
		// Set up Users
		User user1 = new User(
			"user1",
			"baekjoon1",
			"github1",
			"20210001",
			"password",
			"discord",
			15,
			"notion",
			"11550"
		);
		User user2 = new User(
			"user2",
			"baekjoon2",
			"github2",
			"20210002",
			"password",
			"discord",
			15,
			"11551",
			"notion"
		);

		// Set up Algorithms
		Algorithm algorithm1 = new Algorithm(1, 1, "Algorithm 1", 2, null);
		Algorithm algorithm2 = new Algorithm(2, 2, "Algorithm 2", 2, null);

		// Set up Problems
		Problem problem1 = new Problem("Problem 1", 3, algorithm1, null, null);
		Problem problem2 = new Problem("Problem 2", 4, algorithm2, null, null);
		problem1.setId(1L);
		problem2.setId(2L);
		problems = Arrays.asList(problem1, problem2);

		// Set up ProblemResponseDtos
		problemResponseDtos = Arrays.asList(
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
			SolvedProblem.builder().user(user1).problem(problem1).build(),
			SolvedProblem.builder().user(user2).problem(problem1).build()
		);
	}

	@Test
	@DisplayName("문제 푼 유저 목록 조회 테스트")
	void getSolvedUserListByProblemId_shouldReturnListOfSolvedUserResponseDto() {
		// Given
		Long problemId = 1L;
		List<SolvedUserResponseDto> expectedUsers = solvedUsers;
		when(problemSolvingService.getSolvedUserListByProblemId(problemId)).thenReturn(solvedProblems);
		System.out.println(solvedProblems.size());

		// problemMapper.mapToSolvedUserResponseDto() 모킹 추가
		for (int i = 0; i < solvedProblems.size(); i++) {
			when(problemMapper.mapToSolvedUserResponseDto(solvedProblems.get(i).getUser(), solvedProblems.get(i)))
				.thenReturn(expectedUsers.get(i));
			System.out.println(solvedProblems.get(i).getUser().getUsername());
		}

		// When
		List<SolvedUserResponseDto> actualUsers = problemFacade.getSolvedUserListByProblemId(problemId);

		// Then
		assertEquals(expectedUsers, actualUsers);
	}

}