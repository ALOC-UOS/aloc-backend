package com.aloc.aloc.problem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.user.dto.response.SolvedUserResponseDto;

@ExtendWith(MockitoExtension.class)
public class ProblemServiceTest {

	@Mock
	private ProblemFacade problemFacade;

	@InjectMocks
	private ProblemService problemService;

	@Test
	@DisplayName("문제 목록 조회 성공 테스트")
	void getProblems_shouldReturnListOfProblemResponseDto() {
		// given
		List<ProblemResponseDto> expectedProblems = Arrays.asList(
			new ProblemResponseDto(1L, "Problem 1", null, 3, 100),
			new ProblemResponseDto(2L, "Problem 2", null, 4, 50)
		);
		// when
		when(problemFacade.getVisibleProblemsWithSolvingCount()).thenReturn(expectedProblems);
		List<ProblemResponseDto> actualProblems = problemService.getProblems();

		// then
		assertEquals(expectedProblems, actualProblems);
	}

	@Test
	@DisplayName("문제 푼 유저 목록 조회 테스트")
	void getSolvedUserListByProblemId_shouldReturnListOfSolvedUserResponseDto() {
		// Given
		Long problemId = 1L;
		List<SolvedUserResponseDto> expectedUsers = Arrays.asList(
			SolvedUserResponseDto.builder()
				.username("user1")
				.githubId("github1")
				.baekjoonId("baekjoon1")
				.profileColor("blue")
				.studentId("20210001")
				.profileNumber(1)
				.rank(10)
				.coin(100)
				.solvedAt("11:30:00")
				.build(),
			SolvedUserResponseDto.builder()
				.username("user2")
				.githubId("github2")
				.baekjoonId("baekjoon2")
				.profileColor("red")
				.studentId("20210002")
				.profileNumber(2)
				.rank(20)
				.coin(200)
				.solvedAt("12:45:00")
				.build()
		);
		when(problemFacade.getSolvedUserList(problemId)).thenReturn(expectedUsers);
		// When
		List<SolvedUserResponseDto> actualUsers = problemService.getSolvedUserListByProblemId(problemId);
		// Then
		assertEquals(expectedUsers, actualUsers);
	}

	@Test
	@DisplayName("문제 목록 조회 실패 테스트")
	void getSolvedUserListByInvalidProblemId_shouldThrowIllegalArgumentException() {
		// Given
		Long problemId = 1L;
		// When
		when(problemFacade.getSolvedUserList(problemId)).thenThrow(new IllegalArgumentException("해당 문제가 존재하지 않습니다."));
		// Then
		assertThrows(IllegalArgumentException.class, () -> problemService.getSolvedUserListByProblemId(problemId));
	}
}
