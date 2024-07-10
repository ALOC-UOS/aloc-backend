package com.aloc.aloc.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aloc.aloc.algorithm.entity.Algorithm;
import com.aloc.aloc.algorithm.service.AlgorithmService;
import com.aloc.aloc.color.Color;
import com.aloc.aloc.color.service.ColorService;
import com.aloc.aloc.problem.service.ProblemFacade;
import com.aloc.aloc.problem.service.ProblemService;
import com.aloc.aloc.problem.service.ProblemSolvingService;
import com.aloc.aloc.problemtype.enums.Course;
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
	private final AlgorithmService algorithmService;
	private final ProblemService problemService;
	private final ProblemFacade problemFacade;
	private final ProblemSolvingService problemSolvingService;
	private final ColorService colorService;

	@Value("${app.season}")
	private int season;
	private int thisWeekUnsolvedCount;


	public List<UserDetailResponseDto> getUsers() {
		List<Authority> authorities = List.of(Authority.ROLE_USER, Authority.ROLE_ADMIN);
		List<User> users = userRepository.findAllByAuthorityIn(authorities);
		List<User> sortingUsers = userSortingService.sortUserList(users);
		return sortingUsers.stream().map( user -> {
			int solvedCount = problemSolvingService.getSolvedCount(user.getId());
			int totalProblemCount = problemService.getTotalProblemCount(user.getCourse());
			int unsolvedCount = totalProblemCount - solvedCount;
			getThisWeekCount(user.getId(), user.getCourse());
//			int thisWeekProblemCount, thisWeekUnSolvedCount =  getThisWeekCount(user.getId(), user.getCourse());
			// Color 데이터 가져오기
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
				.unsolved(unsolvedCount)
				.todayUnsolved(problemFacade.getTodayProblemSolved(user.getId(), user.getCourse()))
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
		}).collect(Collectors.toList());
	}

	private void getThisWeekCount(Long userId, Course course) {
		// 이번주에 푼 문제, 안 푼 문제들의 횟수를 가져옵니다.
		Algorithm algorithm = algorithmService.getAlgorithmBySeason(season).orElseThrow();
		List<Integer> thisWeekData =
			problemFacade.getThisWeekSolvedCount(userId, algorithm.getAlgorithmId(), season, course);

		// 나중에 필요할 경우를 대비해 남겨둠
//		int thisWeekSolvedCount = thisWeekData.get(0);
//		int thisWeekProblemCount = thisWeekData.get(1);
		thisWeekUnsolvedCount = thisWeekData.get(2);
	}
}
