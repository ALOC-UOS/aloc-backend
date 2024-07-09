package com.aloc.aloc.problem.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
public class ProblemRepositoryTest {
	@Autowired
	private EntityManager em;

	@Autowired
	private ProblemRepository problemRepository;

	private ProblemDummyEntity dummyEntity;

	@BeforeEach
	void setUp() {
		dummyEntity = new ProblemDummyEntity();
		dummyEntity.setupEntities(em);
	}

	@Test
	@Tag("Repository")
	@DisplayName("공개된 문제를 생성일 기준 정렬하여 가져오기")
	void findAllByHiddenIsNullOrderByCreatedAtDesc_ShouldReturnNonHiddenProblemsOrderedByCreatedAtDesc() {
		// when
		List<Problem> problems = problemRepository.findAllByHiddenIsFalseOrderByCreatedAtDesc();

		// then
		assertThat(problems).hasSize(3);

		// Problem 4이 가장 마지막에 생성되었으므로 가장 앞에 위치해야 함
		assertThat(problems.get(0).getId()).isEqualTo(dummyEntity.problem4.getId());
		assertThat(problems.get(1).getId()).isEqualTo(dummyEntity.problem3.getId());
	}

	@Test
	@Tag("Repository")
	@DisplayName("문제 유형을 통해 공개된 문제 중 가장 최근에 업데이트된 문제 찾기")
	void findLatestPublicProblemByProblemTypeId_ShouldReturnLatestProblem() {
		// when
		Problem latestProblem =
			problemRepository.findLatestPublicProblemByProblemTypeId(dummyEntity.problemType1.getId());

		// then
		assertThat(latestProblem.getId()).isEqualTo(dummyEntity.problem1.getId());
	}

	@Test
	@Tag("Repository")
	@DisplayName("알고리즘 ID와 코스를 통해 문제가 존재하는지 확인")
	void existsByAlgorithmIdAndProblemType_Course_ShouldReturnTrue() {
		// when
		boolean existsFull =
			problemRepository.existsByProblemIdAndProblemType_Course(1, Course.FULL);
		boolean existsHalf =
			problemRepository.existsByProblemIdAndProblemType_Course(3, Course.HALF);

		assertTrue(existsFull);
		assertTrue(existsHalf);
	}

	@Test
	@Tag("Repository")
	@DisplayName("문제 ID와 코스를 통해 문제가 존재하지 않는지 확인")
	void existsByProblemIdAndProblemType_Course_ShouldReturnFalse() {
		// when : 존재하지 않는 문제 ID로 확인
		boolean exists = problemRepository.existsByProblemIdAndProblemType_Course(5, Course.FULL);

		// then
		assertFalse(exists);
	}

	@Test
	@Tag("Repository")
	@DisplayName("숨겨진 문제 중 루틴에 따라 정렬하여 가져오기")
	void findAllByHiddenIsTrueAndProblemType_RoutineOrderByIdAsc_ShouldReturnHiddenProblems() {
		List<Problem> hiddenProblems =
			problemRepository.findAllByHiddenIsTrueAndProblemType_RoutineOrderByIdAsc(Routine.DAILY);
		assertThat(hiddenProblems).hasSize(1);
		assertThat(hiddenProblems.get(0).getId()).isEqualTo(dummyEntity.problem2.getId());
	}
}
