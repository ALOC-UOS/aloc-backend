package com.aloc.aloc.problem.service;

import com.aloc.aloc.problemtype.enums.Routine;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.SolvedProblem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.problem.repository.ProblemSolvingCountProjection;
import com.aloc.aloc.problem.repository.SolvedProblemRepository;
import com.aloc.aloc.tag.dto.TagSimpleDto;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.dto.response.SolvedUserResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemFacade {
	private final ProblemRepository problemRepository;
	private final SolvedProblemRepository solvedProblemRepository;
	public List<ProblemResponseDto> getVisibleProblemsWithSolvingCount() {
		List<Problem> problems = problemRepository.findAllByHiddenIsNullOrderByCreatedAtDesc();
		Map<Long, Integer> solvingCountMap = getSolvingCountMap();

		return problems.stream()
			.map(problem -> mapToProblemResponseDto(problem, solvingCountMap))
			.collect(Collectors.toList());
	}

	private Map<Long, Integer> getSolvingCountMap() {
		return solvedProblemRepository.countSolvingUsersByProblem().stream()
			.collect(Collectors.toMap(
				ProblemSolvingCountProjection::getProblemId,
				ProblemSolvingCountProjection::getSolvingCount
			));
	}

	private ProblemResponseDto mapToProblemResponseDto(Problem problem, Map<Long, Integer> solvingCountMap) {
		return ProblemResponseDto.builder()
			.id(problem.getId())
			.title(problem.getTitle())
			.tags(problem.getProblemTagList().stream()
			.map(problemTag -> TagSimpleDto.builder()
				.id(problemTag.getTag().getId())
				.koreanName(problemTag.getTag().getKoreanName())
				.englishName(problemTag.getTag().getEnglishName())
				.build())
			.collect(Collectors.toList()))
			.difficulty(problem.getDifficulty())
			.solvingCount(solvingCountMap.getOrDefault(problem.getId(), 0))
			.build();
	}

	private void checkProblemExist(Long problemId) {
		Optional<Problem> problem = problemRepository.findById(problemId);
		if (problem.isEmpty()) {
			throw new IllegalArgumentException("해당 문제가 존재하지 않습니다.");
		}
	}

	public List<SolvedUserResponseDto> getSolvedUserList(Long problemId) {
		checkProblemExist(problemId);
		List<SolvedProblem> solvedProblems = solvedProblemRepository.findAllByProblemId(problemId);
		return solvedProblems.stream()
			.map(solvedProblem -> {
				User user = solvedProblem.getUser();
				return SolvedUserResponseDto.builder()
					.username(user.getUsername())
					.githubId(user.getGithubId())
					.baekjoonId(user.getBaekjoonId())
					.profileColor(user.getProfileColor())
					.studentId(user.getStudentId())
					.profileNumber(user.getProfileNumber())
					.rank(user.getRank())
					.coin(user.getCoin())
					.solvedAt(
						solvedProblem.getSolvedAt().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
					.build();
			})
			.collect(Collectors.toList());
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
}
