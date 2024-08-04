package com.aloc.aloc.problem.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.service.AlgorithmService;
import com.aloc.aloc.problem.dto.request.ProblemRequestDto;
import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.problemtag.ProblemTag;
import com.aloc.aloc.problemtag.repository.ProblemTagRepository;
import com.aloc.aloc.problemtype.ProblemType;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;
import com.aloc.aloc.problemtype.repository.ProblemTypeRepository;
import com.aloc.aloc.tag.Tag;
import com.aloc.aloc.tag.repository.TagRepository;
import com.aloc.aloc.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemService {
	private final ProblemRepository problemRepository;
	private final ProblemTypeRepository problemTypeRepository;
	private final ProblemMapper problemMapper;
	private final TagRepository tagRepository;
	private final ProblemTagRepository problemTagRepository;
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

	List<Problem> getWeeklyProblem(User user) {
		Algorithm weeklyAlgorithm = algorithmService.findWeeklyAlgorithm();
		ProblemType problemType = problemTypeRepository
			.findProblemTypeByCourseAndRoutine(user.getCourse(), Routine.WEEKLY)
			.orElseThrow(() -> new IllegalArgumentException("해당 코스의 주간 문제 타입이 없습니다."));
		System.out.println("problemType = " + problemType.getId());
		return problemRepository.findVisibleProblemsByAlgorithmAndCourse(
			currentSeason,
			weeklyAlgorithm.getAlgorithmId(),
			problemType.getId()
		);
	}

	public List<ProblemResponseDto> getVisibleDailyProblemsDtoByAlgorithmId(
		int season, int algorithmId, Course course
	) {
		List<Problem> problems = getVisibleDailyProblemsByAlgorithmId(season, algorithmId, course);
		return problems.stream()
			.map(problemMapper::mapToProblemResponseDto)
			.collect(Collectors.toList());
	}

	public void checkProblemExist(Long id) {
		Optional<Problem> problem = problemRepository.findById(id);
		if (problem.isEmpty()) {
			throw new IllegalArgumentException("해당 문제가 존재하지 않습니다.");
		}
	}

	public Boolean checkProblemExistByProblemId(Integer problemId) {
		return problemRepository.existsProblemByProblemIdAndAlgorithm_Season(problemId, currentSeason);
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

	public Integer updateProblemHiddenFalse(Routine routine) {
		if (routine.equals(Routine.DAILY)) {
			Problem halfTodayProblem = problemRepository.findFirstHiddenProblemByCourseAndRoutine(Course.HALF, routine);
			Problem fullTodayProblem = problemRepository.findFirstHiddenProblemByCourseAndRoutine(Course.FULL, routine);
			halfTodayProblem.setHidden(false);
			fullTodayProblem.setHidden(false);
			problemRepository.save(halfTodayProblem);
			problemRepository.save(fullTodayProblem);
			return 2;
		} else {
			List<Problem> problems = problemRepository.findAllByHiddenIsTrueAndProblemType_RoutineOrderByIdAsc(routine);
			for (Problem problem : problems) {
				problem.setHidden(false);
			}
			problemRepository.saveAll(problems);
			return problems.size();
		}
	}

	public Integer getTotalProblemCount(Course course) {
		// 해당 코스의 공개 된 모든 문제 수를 가져옵니다.
		return problemRepository.countAllByCourse(currentSeason, course);
	}

	public List<Problem> getVisibleProblemsBySeasonAndCourse(Course course) {
		return problemRepository.findVisibleProblemsBySeasonAndCourse(currentSeason, course);
	}

	public boolean isNewProblem(String problemId, ProblemType problemType, Integer season) {
		Integer problemIdInt = Integer.parseInt(problemId);
		return problemRepository.notExistsByProblemIdAndCourseAndSeason(
			problemIdInt, problemType.getCourse(), season);
	}

	public Problem saveProblem(Problem problem) {
		problemRepository.save(problem);
		return problem;
	}

	public List<Problem> getHiddenProblemsBySeasonAndCourse(Course course) {
		return problemRepository.findHiddenProblemsBySeasonAndCourse(currentSeason, course);
	}

	public Problem addProblem(ProblemRequestDto problemRequestDto) {
		Algorithm algorithm = algorithmService.getAlgorithmByName(problemRequestDto.getAlgorithm());
		ProblemType problemType = problemTypeRepository.findById(problemRequestDto.getProblemTypeId())
			.orElseThrow(() -> new IllegalArgumentException("해당 문제 타입이 없습니다."));
		if (checkProblemExistByProblemId(problemRequestDto.getProblemId())) {
			throw new IllegalArgumentException("해당 문제가 이미 이번 시즌에 존재합니다.");
		}
		return createProblem(problemRequestDto, algorithm, problemType);
	}

	private void saveProblemTag(Problem problem, List<String> tags) {
		for (String tagName : tags) {
			Tag tag = tagRepository.findByKoreanName(tagName)
				.orElseThrow(() -> new IllegalArgumentException("해당 태그가 없습니다."));
			ProblemTag problemTag = ProblemTag.builder()
				.problem(problem)
				.tag(tag)
				.build();
			problemTagRepository.save(problemTag);
			problem.addProblemTag(problemTag);
		}
	}

	private Problem createProblem(
		ProblemRequestDto problemRequestDto,
		Algorithm algorithm,
		ProblemType problemType
	) {
		Problem problem = Problem.builder()
			.title(problemRequestDto.getTitle())
			.algorithm(algorithm)
			.problemId(problemRequestDto.getProblemId())
			.difficulty(problemRequestDto.getDifficulty())
			.problemType(problemType)
			.build();
		problemRepository.save(problem);
		saveProblemTag(problem, problemRequestDto.getTags());
		return problem;
	}
}

