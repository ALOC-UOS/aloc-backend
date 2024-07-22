package com.aloc.aloc.user.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aloc.aloc.color.Color;
import com.aloc.aloc.color.service.ColorService;
import com.aloc.aloc.history.service.HistoryService;
import com.aloc.aloc.problem.entity.Problem;
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
	private final UserService userService;
	private final HistoryService historyService;

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

	ProblemCounts getProblemCounts(User user) {
		List<Integer> counts = problemService.getTotalProblemCount(user.getCourse());
		return new ProblemCounts(counts.get(0), counts.get(1));
	}

	public record ProblemCounts(int totalWeeklyCount, int totalDailyCount) {
	}

	private void loadNewUserProblemRecord(User user) throws IOException {
		List<Problem> problems = problemService.getVisibleProblemsBySeasonAndCourse(user.getCourse());
		for (Problem problem : problems) {
			problemSolvingService.addUserProblem(user, problem);
		}
	}

	public String addUser(String username, String githubId) throws IOException {
		userService.checkAdmin(username);
		Optional<User> userOptional = userRepository.findByGithubId(githubId);
		if (userOptional.isEmpty()) {
			throw new IllegalArgumentException("해당 깃허브 아이디로 가입된 사용자가 없습니다.");
		}
		User user = userOptional.get();
		if (Authority.ROLE_USER.equals(user.getAuthority())) {
			throw new IllegalArgumentException("이미 등록된 멤버입니다.");
		}
		user.setAuthority(Authority.ROLE_USER);
		loadNewUserProblemRecord(user);
		historyService.addHistory(user, "plusMember", null);
		return "스터디 멤버로 등록되었습니다.";
	}
}
