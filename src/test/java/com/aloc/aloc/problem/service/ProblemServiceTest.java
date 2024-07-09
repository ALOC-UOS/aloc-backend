package com.aloc.aloc.problem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
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

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.user.User;

@ExtendWith(MockitoExtension.class)
public class ProblemServiceTest {

	@InjectMocks
	private ProblemService problemService;

	@Mock
	private ProblemRepository problemRepository;

	@Mock
	private ProblemMapper problemMapper;

	private List<Problem> problems;
	private List<ProblemResponseDto> problemResponseDtos;

	@BeforeEach
	void setUp() {
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
	}

	@Test
	@DisplayName("문제 목록 조회 성공 테스트")
	void getProblems_shouldReturnListOfProblemResponseDto() {
		// given
		Problem p1 = problems.get(0);
		Problem p2 = problems.get(1);
		ProblemResponseDto dto1 = problemResponseDtos.get(0);
		ProblemResponseDto dto2 = problemResponseDtos.get(1);
		when(problemRepository.findAllByHiddenIsFalseOrderByCreatedAtDesc()).thenReturn(problems);
		when(problemMapper.mapToProblemResponseDto(p1)).thenReturn(dto1);
		when(problemMapper.mapToProblemResponseDto(p2)).thenReturn(dto2);

		// When
		List<ProblemResponseDto> result = problemService.getVisibleProblemsWithSolvingCount();

		// Then
		assertEquals(2, result.size());
		assertEquals("Problem 1", result.get(0).getTitle());
		assertEquals("Problem 2", result.get(1).getTitle());

		verify(problemRepository).findAllByHiddenIsFalseOrderByCreatedAtDesc();
		verify(problemMapper, times(2)).mapToProblemResponseDto(any(Problem.class));

	}

	@Test
	@DisplayName("문제 목록 조회 실패 테스트")
	void getSolvedUserListByInvalidProblemId_shouldThrowIllegalArgumentException() {
		// Given
		Long problemId = 1L;
		// When
		when(problemRepository.findById(problemId)).thenReturn(Optional.empty());
		// Then
		assertThrows(IllegalArgumentException.class, () -> problemService.checkProblemExist(problemId));
	}
}
