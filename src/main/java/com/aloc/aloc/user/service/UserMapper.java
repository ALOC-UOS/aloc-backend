package com.aloc.aloc.user.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.aloc.aloc.color.Color;
import com.aloc.aloc.color.service.ColorService;
import com.aloc.aloc.problem.service.ProblemFacade;
import com.aloc.aloc.problem.service.ProblemSolvingService;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {
	private final ProblemSolvingService problemSolvingService;
	private final ProblemFacade problemFacade;
	private final ColorService colorService;

	UserDetailResponseDto mapToUserDetailResponseDto(User user) {
		int solvedCount = problemSolvingService.getSolvedCountByUserId(user.getId());
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
			.unsolvedCount(problemCounts.totalDailyCount() - solvedCount) // TODO: 지금은 데일리만 제공, 나중엔 위클리도 함께 제공
			.todaySolved(problemFacade.getTodayProblemSolved(user.getId(), user.getCourse())) // 오늘 문제 풀었는지 여부
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

	public record ProblemCounts(int totalWeeklyCount, int totalDailyCount) {
	}

	ProblemCounts getProblemCounts(User user) {
		List<Integer> counts = problemFacade.getTotalProblemCount(user.getCourse());
		return new ProblemCounts(counts.get(0), counts.get(1));
	}

}
