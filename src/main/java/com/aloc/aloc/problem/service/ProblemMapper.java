package com.aloc.aloc.problem.service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserProblem;
import com.aloc.aloc.problem.repository.UserProblemRepository;
import com.aloc.aloc.problemtag.ProblemTag;
import com.aloc.aloc.tag.Tag;
import com.aloc.aloc.tag.dto.TagSimpleDto;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.dto.response.SolvedUserResponseDto;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class ProblemMapper {
	private final UserProblemRepository userProblemRepository;

	@Value("${app.season}")
	private Integer season;

	ProblemResponseDto mapToProblemResponseDto(Problem problem) {
		return ProblemResponseDto.builder()
			.id(problem.getId())
			.title(problem.getTitle())
			.tags(mapToTagSimpleDtoList(problem.getProblemTagList()))
			.difficulty(problem.getDifficulty())
			.solvingCount(userProblemRepository.countSolvingUsersByProblemId(problem.getId(), season))
			.build();
	}

	private List<TagSimpleDto> mapToTagSimpleDtoList(List<ProblemTag> problemTags) {
		return problemTags.stream()
			.map(this::mapToTagSimpleDto)
			.collect(Collectors.toList());
	}

	private TagSimpleDto mapToTagSimpleDto(ProblemTag problemTag) {
		Tag tag = problemTag.getTag();
		return TagSimpleDto.builder()
			.id(tag.getId())
			.koreanName(tag.getKoreanName())
			.englishName(tag.getEnglishName())
			.build();
	}

	SolvedUserResponseDto mapToSolvedUserResponseDto(User user, UserProblem userProblem) {
		return SolvedUserResponseDto.builder()
			.username(user.getUsername())
			.githubId(user.getGithubId())
			.baekjoonId(user.getBaekjoonId())
			.profileColor(user.getProfileColor())
			.studentId(user.getStudentId())
			.profileNumber(user.getProfileNumber())
			.rank(user.getRank())
			.coin(user.getCoin())
			.solvedAt(userProblem.getSolvedAt().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
			.build();
	}

	// 안 푼 문제 + 푼 문제 모두 합쳐져 있음.
	// TODO: 안쓰면 삭제
	List<ProblemSolvedResponseDto> mapToProblemSolvedResponseDtoList(
		List<Problem> problems, List<UserProblem> solvedProblems
	) {
		// 풀린 문제의 ID 집합을 생성합니다.
		Set<Long> solvedProblemIds = solvedProblems.stream()
			.map(solvedProblem -> solvedProblem.getProblem().getId())
			.collect(Collectors.toSet());

		// 각 문제를 ProblemResponseDto로 변환합니다.
		return problems.stream()
			.map(problem -> ProblemSolvedResponseDto.builder()
				.problemId(problem.getId())
				.problemTitle(problem.getTitle())
				.problemDifficulty(problem.getDifficulty())
				.isSolved(solvedProblemIds.contains(problem.getId()))
				.build())
			.collect(Collectors.toList());
	}

	// TODO: 수정
	List<ProblemSolvedResponseDto> mapSolvedProblemToDtoList(
		List<UserProblem> solvedProblemList
	) {
		return solvedProblemList.stream()
			.map(solvedProblem -> {
				Problem problem = solvedProblem.getProblem();
				return ProblemSolvedResponseDto.builder()
					.problemId(problem.getId())
					.problemTitle(problem.getTitle())
					.problemDifficulty(problem.getDifficulty())
					.isSolved(true)
					.build();
			})
			.collect(Collectors.toList());
	}

	// TODO: 수정
	public ProblemSolvedResponseDto mapToProblemSolvedResponseDto(Problem problem, boolean isSolved) {
		return ProblemSolvedResponseDto.builder()
			.problemId(problem.getId())
			.problemTitle(problem.getTitle())
			.problemDifficulty(problem.getDifficulty())
			.isSolved(isSolved)
			.build();
	}
}
