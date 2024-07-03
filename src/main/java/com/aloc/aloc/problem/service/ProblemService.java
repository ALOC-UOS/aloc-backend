package com.aloc.aloc.problem.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.SolvedProblem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.problem.repository.SolvedProblemRepository;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;
import com.aloc.aloc.problemtype.repository.ProblemTypeRepository;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.dto.response.SolvedUserResponseDto;
import com.aloc.aloc.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemService {
	private final ProblemSolvingService problemSolvingService;
	private final UserRepository userRepository;
	private final ProblemRepository problemRepository;
	private final ProblemTypeRepository problemTypeRepository;
	private final SolvedProblemRepository solvedProblemRepository;
	private final ProblemMapper problemMapper;

	private User findUser(String username) {
		return userRepository.findByGithubId(username)
			.orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다."));
	}

	public List<ProblemResponseDto> getVisibleProblemsWithSolvingCount() {
		// 공개된 문제 목록을 정렬하여 가져옵니다.
		List<Problem> problems = problemRepository.findAllByHiddenIsNullOrderByCreatedAtDesc();
		return problems.stream()
			.map(problemMapper::mapToProblemResponseDto)
			.collect(Collectors.toList());
	}

	public List<SolvedUserResponseDto> getSolvedUserListByProblemId(Long problemId) {
		// 문제가 존재하는지 확인합니다.
		checkProblemExist(problemId);

		// 문제를 푼 사용자 목록을 가져옵니다.
		List<SolvedProblem> solvedProblems = solvedProblemRepository.findAllByProblemId(problemId);
		return solvedProblems.stream()
			.map(solvedProblem -> {
				User user = solvedProblem.getUser();
				return problemMapper.mapToSolvedUserResponseDto(user, solvedProblem);
			})
			.collect(Collectors.toList());
	}

	private void checkProblemExist(Long problemId) {
		Optional<Problem> problem = problemRepository.findById(problemId);
		if (problem.isEmpty()) {
			throw new IllegalArgumentException("해당 문제가 존재하지 않습니다.");
		}
	}


	public ProblemResponseDto findTodayProblemByCourse(Course course) {
		// 오늘의 문제 타입을 가져옵니다.
		Long problemTypeId =
			problemTypeRepository.findProblemTypeByCourseAndRoutine(course, Routine.DAILY).getId();

		// 오늘의 문제를 가져옵니다.
		Problem todayProblem = problemRepository.findLatestPublicProblemByProblemTypeId(problemTypeId);

		// 오늘의 문제가 없으면 오류를 발생시킵니다.
		if (todayProblem == null) {
			throw new IllegalArgumentException("오늘의 문제가 없습니다.");
		}
		return problemMapper.mapToProblemResponseDto(todayProblem);
	}

	public String checkSolved(String username) throws IOException {
		User user = findUser(username);
		// 오늘의 문제를 가져옵니다.
		ProblemResponseDto problem = findTodayProblemByCourse(user.getCourse());

		// 오늘의 문제가 없으면 오류를 발생시킵니다.
		if (problem == null) {
			throw new IllegalArgumentException("오늘의 문제가 없습니다.");
		}

		// 문제를 풀었는지 확인합니다.
		return problemSolvingService.checkAndUpdateProblemSolved(problem, user);
	}
}
