package com.aloc.aloc.problem.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.dto.response.SolvedUserResponseDto;
import com.aloc.aloc.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemFacade implements UserProblemRecordLoader {
	private final ProblemService problemService;
	private final UserService userService;
	private final ProblemMapper problemMapper;
	private final ProblemSolvingService problemSolvingService;

	public String checkSolved(String username) {
		// 오늘의 문제와 다른 문제들의 풀이 여부를 한번에 확인합니다.
		User user = userService.findUser(username);
		loadUserProblemRecord(user);
		return "success";
	}

	public List<SolvedUserResponseDto> getSolvedUserListByProblemId(Long problemId) {
		problemService.checkProblemExist(problemId);
		return problemSolvingService.getSolvedUserListByProblemId(problemId)
			.stream()
			.map(solvedProblem -> problemMapper.mapToSolvedUserResponseDto(solvedProblem.getUser(), solvedProblem))
			.collect(Collectors.toList());
	}

	public List<ProblemSolvedResponseDto> getWeeklyCompletionStatus(String username) {
		User user = userService.findUser(username);
		return problemSolvingService.getWeeklyCompletionStatus(user);
	}

	public List<ProblemSolvedResponseDto> getUnsolvedProblemListByUser(
		String githubId, Integer season, Routine routine
	) {
		User user = userService.findUser(githubId);
		return problemSolvingService.getUnsolvedProblemListByUser(user, season, routine);
	}

	public List<ProblemSolvedResponseDto> getSolvedProblemListByUser(String githubId, Integer season, Routine routine) {
		User user = userService.findUser(githubId);
		return problemSolvingService.getSolvedProblemListByUser(user, season, routine);
	}

	public List<Integer> getThisWeekSolvedCount(User user) {
		return problemSolvingService.getThisWeekSolvedCount(user);

	}

	public Boolean getTodayProblemSolved(Long userId, Course course) {
		return problemSolvingService.getTodayProblemSolved(userId, course);
	}

	public Integer getTotalProblemCountByCourse(Course course) {
		// 해당 코스의 공개 된 모든 문제 수를 가져옵니다.
		return problemService.getTotalProblemCount(course);
	}

	@Override
	public void loadUserProblemRecord(User user) {
		List<Problem> problems = problemService.getVisibleProblemsBySeasonAndCourse(user.getCourse());
		Problem todayProblem = problemService.findTodayProblemByCourse(user.getCourse());
		for (Problem problem : problems) {
			boolean problemSolved =
				problemSolvingService.updateUserAndSaveSolvedProblem(user, problem, todayProblem.getId());
			if (problemSolved) {
				user.addSolvedCount();
				userService.checkUserRank(user);
				userService.saveUser(user);
			}
		}
	}

	public void updateAllUserProblem() {
		List<User> activeUsers = userService.getActiveUsers();
		for (User user : activeUsers) {
			problemSolvingService.addUserProblemRecord(user);
		}
	}
}
