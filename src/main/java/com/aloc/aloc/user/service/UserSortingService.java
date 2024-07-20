package com.aloc.aloc.user.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.aloc.aloc.problem.entity.UserProblem;
import com.aloc.aloc.problem.repository.UserProblemRepository;
import com.aloc.aloc.user.User;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class UserSortingService {
	private final UserProblemRepository userProblemRepository;

	public List<User> sortUserList(List<User> userList) {
		return userList.stream()
			.sorted(Comparator.comparing(this::sortByRank)
				.thenComparing(this::getLatestSolvedTime, Comparator.nullsLast(Comparator.reverseOrder())))
			.collect(Collectors.toList());
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
