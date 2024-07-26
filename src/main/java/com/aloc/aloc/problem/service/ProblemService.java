package com.aloc.aloc.problem.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.service.AlgorithmService;
import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.problemtype.ProblemType;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;
import com.aloc.aloc.problemtype.repository.ProblemTypeRepository;
import com.aloc.aloc.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemService {
	private final ProblemRepository problemRepository;
	private final ProblemTypeRepository problemTypeRepository;
	private final ProblemMapper problemMapper;
	private final AlgorithmService algorithmService;

	@Value("${app.season}")
	private Integer currentSeason;
	public Problem findProblemById(Long problemId) {
		return problemRepository.findById(problemId)
			.orElseThrow(() -> new IllegalArgumentException("문제가 없습니다."));
	}

	List<Problem> getVisibleDailyProblemsByAlgorithmId(int season, int algorithmId, Course course) {
		ProblemType problemType = problemTypeRepository.findProblemTypeByCourseAndRoutine(course, Routine.DAILY)
			.orElseThrow(() -> new IllegalArgumentException("문제 타입이 없습니다.")
		);
		return problemRepository.findVisibleProblemsByAlgorithmAndCourse(season, algorithmId, problemType.getId());
	}

	public List<ProblemResponseDto> getVisibleProblemsDtoByAlgorithmId(int season, int algorithmId, Course course) {
		List<Problem> problems = getVisibleDailyProblemsByAlgorithmId(season, algorithmId, course);
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

	public ProblemResponseDto getTodayProblemDto(Course course) {
		return problemMapper.mapToProblemResponseDto(findTodayProblemByCourse(course));
	}

	public Problem findTodayProblemByCourse(Course course) {
		// 오늘의 문제 타입을 가져옵니다.
		Long problemTypeId =
			problemTypeRepository.findProblemTypeByCourseAndRoutine(course, Routine.DAILY)
				.orElseThrow(() -> new IllegalArgumentException("오늘의 문제 타입이 없습니다."))
				.getId();

		// 오늘의 문제를 가져옵니다.
		Problem todayProblem = problemRepository.findLatestPublicProblemByProblemTypeId(problemTypeId);

		// 오늘의 문제가 없으면 오류를 발생시킵니다.
		if (todayProblem == null) {
			throw new IllegalArgumentException("오늘의 문제가 없습니다.");
		}
		return todayProblem;
	}



	@Transactional
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

	public List<Integer> getTotalProblemCount(Course course) {
		// 해당 코스의 공개 된 문제 중 Weekly와 Daily 문제의 개수를 가져옵니다.
		Integer weeklyCount = problemRepository.countAllByCourseAndRoutine(currentSeason, course,
			Routine.WEEKLY);
		Integer dailyCount = problemRepository.countAllByCourseAndRoutine(currentSeason, course,
			Routine.DAILY);
		return List.of(weeklyCount, dailyCount);
	}

	public List<Problem> getVisibleProblemsBySeasonAndCourse(Course course) {
		return problemRepository.findVisibleProblemsBySeasonAndCourse(currentSeason, course);
	}

	// 이번 주차 문제 가져오는 공통 메소드
	List<Problem> getThisWeekProblems(User user) {
		Algorithm thisWeekAlgorithm = algorithmService.getAlgorithmBySeason(currentSeason)
			.orElseThrow(() -> new RuntimeException("해당 주차 알고리즘이 없습니다."));
		return getVisibleDailyProblemsByAlgorithmId(
			currentSeason, thisWeekAlgorithm.getAlgorithmId(), user.getCourse()
		);
	}

	public boolean isNewProblem(String problemId, ProblemType problemType, Integer season) {
		Integer problemIdInt = Integer.parseInt(problemId);
		return problemRepository.notExistsByProblemIdAndCourseAndSeason(
			problemIdInt, problemType.getCourse(), season);
	}

	@Transactional
	public Problem saveProblem(Problem problem) {
		problemRepository.save(problem);
		return problem;
	}

	public List<Problem> getHiddenProblemsBySeasonAndCourse(Course course) {
		return problemRepository.findHiddenProblemsBySeasonAndCourse(currentSeason, course);
	}
}
