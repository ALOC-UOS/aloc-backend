package com.aloc.aloc.problem.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class ProblemRepositoryTest {
  @Autowired private EntityManager em;

  @Autowired private ProblemRepository problemRepository;

  private ProblemDummyEntity dummyEntity;

  @BeforeEach
  void setUp() {
    dummyEntity = new ProblemDummyEntity();
    dummyEntity.setupEntities(em);
  }

  @Test
  @Tag("Repository")
  @DisplayName("공개된 문제를 생성일 기준 정렬하여 가져오기")
  void
      findAllByHiddenIsNullOrderByCreatedAtDescShouldReturnNonHiddenProblemsOrderedByCreatedAtDesc() {
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
  void findLatestPublicProblemByProblemTypeIdShouldReturnLatestProblem() {
    // when
    Problem latestProblem =
        problemRepository.findLatestPublicProblemByProblemTypeId(dummyEntity.problemType1.getId());

    // then
    assertThat(latestProblem.getId()).isEqualTo(dummyEntity.problem1.getId());
  }

  @Test
  @Tag("Repository")
  @DisplayName("알고리즘 ID와 코스를 통해 문제가 존재하는지 확인")
  void existsByAlgorithmIdAndProblemTypeCourseShouldReturnTrue() {
    // when
    boolean existsFull =
        problemRepository.notExistsByProblemIdAndCourseAndSeason(1, Course.FULL, null);
    boolean existsHalf =
        problemRepository.notExistsByProblemIdAndCourseAndSeason(3, Course.HALF, null);

    assertFalse(existsFull);
    assertFalse(existsHalf);
  }

  @Test
  @Tag("Repository")
  @DisplayName("문제 ID와 코스를 통해 문제가 존재하지 않는지 확인")
  void existsByProblemIdAndProblemTypeCourseShouldReturnFalse() {
    // when : 존재하지 않는 문제 ID로 확인
    boolean exists = problemRepository.notExistsByProblemIdAndCourseAndSeason(5, Course.FULL, null);

    // then
    assertTrue(exists);
  }

  @Test
  @Tag("Repository")
  @DisplayName("숨겨진 문제 중 루틴에 따라 정렬하여 가져오기")
  void findAllByHiddenIsTrueAndProblemTypeRoutineOrderByIdAscShouldReturnHiddenProblems() {
    List<Problem> hiddenProblems =
        problemRepository.findAllByHiddenIsTrueAndProblemTypeRoutineOrderByIdAsc(Routine.DAILY);
    assertThat(hiddenProblems).hasSize(1);
    assertThat(hiddenProblems.get(0).getId()).isEqualTo(dummyEntity.problem2.getId());
  }
}
