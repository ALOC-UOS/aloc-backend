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

import lombok.Getter;
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
		List<User> sortedUsers = userSortingService.sortUserList(users);
		return sortedUsers.stream()
			.map(this::mapToUserDetailResponseDto)
			.collect(Collectors.toList());
	}

	private UserDetailResponseDto mapToUserDetailResponseDto(User user) {
		int solvedCount = problemSolvingService.getSolvedCount(user.getId());
		int thisWeekUnsolvedCount = getThisWeekUnsolvedCount(user);
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
			.solved(solvedCount)
			.unsolved(problemCounts.getTotalDailyCount() - solvedCount)
			.todaySolved(problemFacade.getTodayProblemSolved(user.getId(), user.getCourse()))
			.thisWeekUnsolved(thisWeekUnsolvedCount)
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

	private int getThisWeekUnsolvedCount(User user) {
		List<Integer> thisWeekData = problemFacade.getThisWeekSolvedCount(user);
		return thisWeekData.get(2);
	}

	private ProblemCounts getProblemCounts(User user) {
		List<Integer> counts = problemService.getTotalProblemCount(user.getCourse());
		return new ProblemCounts(counts.get(0), counts.get(1));
	}

	@Getter
	private static class ProblemCounts {

		private final int totalWeeklyCount;
		private final int totalDailyCount;

		public ProblemCounts(int totalWeeklyCount, int totalDailyCount) {
			this.totalWeeklyCount = totalWeeklyCount;
			this.totalDailyCount = totalDailyCount;
		}

	}
}
