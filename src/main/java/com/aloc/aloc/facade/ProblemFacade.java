package com.aloc.aloc.facade;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.aloc.aloc.problem.Problem;
import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.solvedproblem.repository.ProblemSolvingCountProjection;
import com.aloc.aloc.solvedproblem.repository.SolvedProblemRepository;
import com.aloc.aloc.tag.dto.TagSimpleDto;

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
}
