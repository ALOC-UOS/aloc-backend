package com.aloc.aloc.user.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.service.ProblemService;
import com.aloc.aloc.problem.service.UserProblemService;
import com.aloc.aloc.user.entity.User;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class UserSortingService {
	private final UserProblemService userProblemService;
	private final ProblemService problemService;

	public List<User> sortUserList(List<User> userList) {
		return userList.stream()
			.sorted(Comparator
				.comparing(this::sortByRank, Comparator.reverseOrder()) // 랭크가 높은 사람 먼저
				.thenComparing(this::hasSolvedToday).reversed() // 오늘 문제 푼 사람 먼저
				// 최근에 푼 사람 먼저
				.thenComparing(this::getLatestSolvedTime, Comparator.nullsLast(Comparator.reverseOrder())))
			.collect(Collectors.toList());
	}

	private LocalDateTime getLatestSolvedTime(User user) {
		return userProblemService.getLatestSolvedTime(user.getId());
	}

	private boolean hasSolvedToday(User user) {
		Problem todayProblem = problemService.findTodayProblemByCourse(user.getCourse());
		return userProblemService.isProblemAlreadySolved(user.getId(), todayProblem.getId());
	}

	private Pair<Integer, Integer> sortByRank(User user) {
		int rank = user.getRank();
		return new Pair<>(-rank / 10, rank % 10);
	}

	private record Pair<T extends Comparable<T>, U extends Comparable<U>>(T first, U second) implements
		Comparable<Pair<T, U>> {

		@Override
		public int compareTo(Pair<T, U> other) {
			int firstComparison = this.first.compareTo(other.first);
			if (firstComparison != 0) {
				return firstComparison;
			}
			return this.second.compareTo(other.second);
		}
	}
}
