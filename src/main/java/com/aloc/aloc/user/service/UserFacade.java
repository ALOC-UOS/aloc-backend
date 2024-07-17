package com.aloc.aloc.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aloc.aloc.color.Color;
import com.aloc.aloc.color.service.ColorService;
import com.aloc.aloc.problem.service.ProblemFacade;
import com.aloc.aloc.problem.service.ProblemService;
import com.aloc.aloc.problem.service.ProblemSolvingService;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.enums.Authority;
import com.aloc.aloc.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserFacade {

	private final UserRepository userRepository;
	private final UserSortingService userSortingService;
	private final ProblemService problemService;
	private final ProblemFacade problemFacade;
	private final ProblemSolvingService problemSolvingService;
	private final ColorService colorService;

	@Value("${app.season}")
	private Integer season;

	public List<UserDetailResponseDto> getUsers() {
		List<Authority> authorities = List.of(Authority.ROLE_USER, Authority.ROLE_ADMIN);
		List<User> users = userRepository.findAllByAuthorityIn(authorities);
		List<User> sortedUserList = userSortingService.sortUserList(users);
		return sortedUserList.stream()
			.map(this::mapToUserDetailResponseDto)
			.collect(Collectors.toList());
	}

	private UserDetailResponseDto mapToUserDetailResponseDto(User user) {
		int solvedCount = problemSolvingService.getSolvedCount(user.getId());
		Integer thisWeekUnsolvedCount = getThisWeekUnsolvedCount(user);
		ProblemCounts problemCounts = getProblemCounts(user);
		Color userColor = colorService.getColorById(user.getProfileColor());

		return UserDetailResponseDto.builder()
			.username(user.getUsername())
			.githubId(user.getGithubId())
			.baekjoonId(user.getBaekjoonId())
			.profileColor(user.getProfileColor())
			.studentId(user.getStudentId())
			.profileNumber(user.getProfileNumber())
			.rank(user.getRank())
			.coin(user.getCoin())
			.solvedCount(solvedCount)
			.unsolvedCount(problemCounts.totalDailyCount() - solvedCount)
			.todaySolvedCount(problemFacade.getTodayProblemSolved(user.getId(), user.getCourse()))
			.thisWeekUnsolvedCount(thisWeekUnsolvedCount)
			.colorCategory(userColor.getCategory())
			.color1(userColor.getColor1())
			.color2(userColor.getColor2())
			.color3(userColor.getColor3())
			.color4(userColor.getColor4())
			.color5(userColor.getColor5())
			.degree(userColor.getDegree())
			.createdAt(user.getCreatedAt())
			.build();
	}

	Integer getThisWeekUnsolvedCount(User user) {
		List<Integer> thisWeekData = problemFacade.getThisWeekSolvedCount(user);
		return thisWeekData.get(2);
	}

	ProblemCounts getProblemCounts(User user) {
		List<Integer> counts = problemService.getTotalProblemCount(user.getCourse());
		return new ProblemCounts(counts.get(0), counts.get(1));
	}

	public record ProblemCounts(int totalWeeklyCount, int totalDailyCount) {
	}
}
