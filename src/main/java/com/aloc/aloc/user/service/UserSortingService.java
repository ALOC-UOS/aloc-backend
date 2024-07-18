package com.aloc.aloc.user.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserProblem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.problem.repository.UserProblemRepository;
import com.aloc.aloc.user.User;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class UserSortingService {
	private final ProblemRepository problemRepository;
	private final UserProblemRepository userProblemRepository;

	public List<User> sortUserList(List<User> userList) {
		// TODO: Weekly 들어오면 수정 예정
		Problem fullCourseProblem = problemRepository.findLatestPublicProblemByProblemTypeId(1L);
		Problem halfCourseProblem = problemRepository.findLatestPublicProblemByProblemTypeId(2L);
		return userList.stream()
			.sorted(Comparator.comparing(this::sortByRank)
				.thenComparing(user -> sortBySolvedTime(user, fullCourseProblem.getId(),
						halfCourseProblem.getId())))
			.collect(Collectors.toList());
	}

	private Pair<Integer, Integer> sortByRank(User user) {
		int rank = user.getRank();
		return new Pair<>(-rank / 10, rank % 10);
	}

	private LocalDateTime sortBySolvedTime(User user, Long fullCourseProblemId, Long halfCourseProblemId) {
		LocalDateTime fullCourseSolvedTime = Optional.ofNullable(fullCourseProblemId)
			.flatMap(id -> userProblemRepository.findByUserIdAndProblemId(user.getId(), id))
			.map(UserProblem::getSolvedAt)
			.orElse(LocalDateTime.now());


		LocalDateTime halfCourseSolvedTime = Optional.ofNullable(halfCourseProblemId)
			.flatMap(id -> userProblemRepository.findByUserIdAndProblemId(user.getId(), id))
			.map(UserProblem::getSolvedAt)
			.orElse(LocalDateTime.now());

		// 두 시간 중 더 이른 시간을 반환
		return fullCourseSolvedTime.isBefore(halfCourseSolvedTime) ? fullCourseSolvedTime : halfCourseSolvedTime;
	}

	private static class Pair<T extends Comparable<T>, U extends Comparable<U>> implements Comparable<Pair<T, U>> {
		private final T first;
		private final U second;

		public Pair(T first, U second) {
			this.first = first;
			this.second = second;
		}

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
