package com.aloc.aloc.problem.service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.SolvedProblem;
import com.aloc.aloc.problem.repository.SolvedProblemRepository;
import com.aloc.aloc.problemtag.ProblemTag;
import com.aloc.aloc.tag.Tag;
import com.aloc.aloc.tag.dto.TagSimpleDto;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.dto.response.SolvedUserResponseDto;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class ProblemMapper {
	private final SolvedProblemRepository solvedProblemRepository;

	ProblemResponseDto mapToProblemResponseDto(Problem problem) {
		return ProblemResponseDto.builder()
			.id(problem.getId())
			.title(problem.getTitle())
			.tags(mapToTagSimpleDtoList(problem.getProblemTagList()))
			.difficulty(problem.getDifficulty())
			.solvingCount(solvedProblemRepository.countSolvingUsersByProblemId(problem.getId()))
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

	SolvedUserResponseDto mapToSolvedUserResponseDto(User user, SolvedProblem solvedProblem) {
		return SolvedUserResponseDto.builder()
			.username(user.getUsername())
			.githubId(user.getGithubId())
			.baekjoonId(user.getBaekjoonId())
			.profileColor(user.getProfileColor())
			.studentId(user.getStudentId())
			.profileNumber(user.getProfileNumber())
			.rank(user.getRank())
			.coin(user.getCoin())
			.solvedAt(solvedProblem.getSolvedAt().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
			.build();
	}
}