package com.aloc.aloc.problem.repository;

import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserProblem;
import com.aloc.aloc.problem.enums.UserProblemStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProblemRepository extends JpaRepository<UserProblem, Long> {

  //	 문제를 푼 사용자 목록을 가져옵니다.
  List<UserProblem> findAllByProblemIdAndUserProblemStatus(
      Long problemId, UserProblemStatus userProblemStatus);

  @Query(
      "SELECT COUNT(DISTINCT up.user) "
          + "FROM UserProblem up WHERE up.problem.id = :problemId "
          + "AND up.userProblemStatus = :status AND up.season = :season")
  int countSolvingUsersByProblemId(
      @Param("problemId") Long problemId,
      @Param("season") int season,
      @Param("status") UserProblemStatus userProblemStatus);

  boolean existsByUserIdAndProblemIdAndUserProblemStatus(
      Long userId, Long problemId, UserProblemStatus userProblemStatus);

  Optional<UserProblem> findByUserIdAndProblemId(Long userId, Long problemId);

  // 시즌이 null 일때는 season에 상관없이 조회
  @Query(
      "SELECT up "
          + "FROM UserProblem up "
          + "JOIN up.problem p "
          + "WHERE up.user.id = :userId "
          + "AND (:season IS NULL OR up.season = :season) "
          + "AND up.userProblemStatus = :status "
          + "AND (p.hidden = false)"
          + "ORDER BY p.createdAt DESC")
  List<UserProblem> findAllByUserIdAndSeasonAndIsSolvedOrderBySolvedAtDesc(
      @Param("userId") Long userId,
      @Param("season") Integer season,
      @Param("status") UserProblemStatus userProblemStatus);

  Optional<UserProblem> findTopByUserIdOrderBySolvedAtDesc(Long userId);

  @Query(
      "SELECT COUNT(up) "
          + "FROM UserProblem up "
          + "WHERE up.problem "
          + "IN :problems "
          + "AND up.userProblemStatus = :status "
          + "AND up.user.id = :userId")
  int countByUnsolvedProblemsIn(
      @Param("problems") List<Problem> problems,
      @Param("userId") Long userId,
      @Param("status") UserProblemStatus userProblemStatus);

  Boolean existsByUserIdAndProblemIdAndSolvedAtAfter(
      Long userId, Long id, LocalDateTime localDateTime);
}
