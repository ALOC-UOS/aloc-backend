package com.aloc.aloc.user.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserProblem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.problem.repository.UserProblemRepository;
import com.aloc.aloc.problemtype.enums.Routine;
import com.aloc.aloc.problemtype.repository.ProblemTypeRepository;
import com.aloc.aloc.user.User;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class UserSortingService {
	private final UserProblemRepository userProblemRepository;
	private final ProblemTypeRepository problemTypeRepository;
	private final ProblemRepository problemRepository;

	public List<User> sortUserList(List<User> userList) {
		return userList.stream()
			.sorted(Comparator
				.comparing(this::sortByRank, Comparator.reverseOrder()) // 랭크가 높은 사람 먼저
				.thenComparing(this::hasSolvedToday).reversed() // 오늘 문제 푼 사람 먼저
				// 최근에 푼 사람 먼저
				.thenComparing(this::getLatestSolvedTime, Comparator.nullsLast(Comparator.reverseOrder())))
			.collect(Collectors.toList());
	}

	private boolean hasSolvedToday(User user) {
		// 유저의 오늘의 문제 타입을 가져옵니다.
		Long problemTypeId =
			problemTypeRepository.findProblemTypeByCourseAndRoutine(user.getCourse(), Routine.DAILY)
				.orElseThrow(() -> new IllegalArgumentException("오늘의 문제 타입이 없습니다."))
				.getId();

		// 오늘의 문제를 가져옵니다.
		Problem todayProblem = problemRepository.findLatestPublicProblemByProblemTypeId(problemTypeId);

		// 오늘의 문제 풀이 여부를 가져옵니다.
		return userProblemRepository.findByUserIdAndProblemId(user.getId(), todayProblem.getId())
			.map(UserProblem::getIsSolved)
			.orElse(false);
	}

	private Pair<Integer, Integer> sortByRank(User user) {
		int rank = user.getRank();
		return new Pair<>(-rank / 10, rank % 10);
	}

	private LocalDateTime getLatestSolvedTime(User user) {
		return userProblemRepository.findTopByUserIdOrderBySolvedAtDesc(user.getId())
			.map(UserProblem::getSolvedAt)
			.orElse(null);
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
