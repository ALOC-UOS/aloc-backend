package com.aloc.aloc.problem.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;
import com.aloc.aloc.problemtype.repository.ProblemTypeRepository;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemService {
	private final UserRepository userRepository;
	private final ProblemRepository problemRepository;
	private final ProblemTypeRepository problemTypeRepository;
	private final ProblemMapper problemMapper;

	User findUser(String username) {
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

	public void checkProblemExist(Long problemId) {
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



	public void updateProblemHiddenFalse(Routine routine) {
		List<Problem> problems = problemRepository.findAllByHiddenIsTrueAndProblemType_RoutineOrderByIdAsc(routine);
		if (routine.equals(Routine.DAILY)) {
			Problem problem = problems.get(0);
			problem.setHidden(false);
			problemRepository.save(problem);
		} else {
			for (Problem problem : problems) {
				problem.setHidden(false);
			}
			problemRepository.saveAll(problems);
		}
	}

	public Long getProblemTypeIdByCourseAndRoutine(Course course, Routine routine) {
		return problemTypeRepository.findProblemTypeByCourseAndRoutine(course, routine).getId();
	}

	List<Problem> getProblemsByAlgorithmWeekAndProblemTypeId(Integer algorithmId, Long problemTypeId) {
		return problemRepository.findAllByAlgorithmWeekAndProblemTypeId(algorithmId, problemTypeId);
	}
}
