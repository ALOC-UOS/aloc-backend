package com.aloc.aloc.problem.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.List;


import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.aloc.aloc.problem.entity.UserProblem;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
public class UserProblemRepositoryTest {
	@Autowired
	private EntityManager em;

	@Autowired
	private UserProblemRepository userProblemRepository;

	private SolvedProblemDummyEntity dummyEntity;
	@BeforeEach
	void setUp() {
		dummyEntity = new SolvedProblemDummyEntity();
		dummyEntity.setupEntities(em);
	}

	@Test
	@Tag("Repository")
	@DisplayName("문제 ID를 통해 해결된 문제 리스트 가져오기")
	void findAllByProblemId_ShouldReturnSolvedProblemList() {
		// when
		List<UserProblem> problems = userProblemRepository.findAllByProblemIdAndIsSolvedIsTrue(dummyEntity.problem1.getId());

		// then
		assertThat(problems).hasSize(2);

		// 푼 사용자 목록을 가져오기 위한 함수, 정렬은 보장되지 않음
		AssertionsForClassTypes.assertThat(problems.get(0).getId()).isEqualTo(dummyEntity.up1.getId());
		AssertionsForClassTypes.assertThat(problems.get(1).getId()).isEqualTo(dummyEntity.up2.getId());
	}

	@Test
	@Tag("Repository")
	@DisplayName("문제 ID를 통해 해결한 사용자 수 가져오기")
	void countSolvingUsersByProblemId_ShouldReturnSolvingUsersCount() {
		// when
		Integer count = userProblemRepository.countSolvingUsersByProblemId(dummyEntity.problem1.getId(), 2);

		// then
		assertThat(count).isEqualTo(2);
	}

	@Test
	@Tag("Repository")
	@DisplayName("사용자 ID와 문제 ID를 통해 해결된 문제가 존재하는지 확인")
	void existsByUserIdAndProblemId_ShouldReturnTrue() {
		Boolean exists = userProblemRepository.existsByUserIdAndProblemIdAndIsSolvedIsTrue(
			dummyEntity.user1.getId(),
			dummyEntity.problem1.getId()
		);
		assertThat(exists).isTrue();
	}

	@Test
	@Tag("Repository")
	@DisplayName("사용자 ID와 문제 ID를 통해 해결된 문제가 존재하지 않는지 확인")
	void existsByUserIdAndProblemId_ShouldReturnFalse() {
		Boolean exists = userProblemRepository.existsByUserIdAndProblemIdAndIsSolvedIsTrue(
			dummyEntity.user1.getId(),
			100L
		);
		assertThat(exists).isFalse();
	}

}
