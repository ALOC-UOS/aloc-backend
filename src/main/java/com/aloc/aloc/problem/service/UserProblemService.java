package com.aloc.aloc.problem.service;

import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserProblem;
import com.aloc.aloc.problem.repository.UserProblemRepository;
import com.aloc.aloc.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProblemService {
  private final UserProblemRepository userProblemRepository;

  @Value("${app.season}")
  private Integer currentSeason;

  public boolean isProblemAlreadySolved(Long userId, Long problemId) {
    // 사용자가 이미 푼 문제인지 확인합니다.
    return userProblemRepository.existsByUserIdAndProblemIdAndIsSolvedIsTrue(userId, problemId);
  }

  public LocalDateTime getLatestSolvedTime(Long userId) {
    return userProblemRepository
        .findTopByUserIdOrderBySolvedAtDesc(userId)
        .map(UserProblem::getSolvedAt)
        .orElse(null);
  }

  public UserProblem getOrCreateUserProblem(User user, Problem problem, boolean isSolved) {
    // 해결 정보가 있으면 업데이트하고 없으면 새로 생성합니다.
    return userProblemRepository
        .findByUserIdAndProblemId(user.getId(), problem.getId())
        .orElse(
            UserProblem.builder()
                .user(user)
                .problem(problem)
                .isSolved(isSolved)
                .season(currentSeason)
                .build());
  }

  public void saveUserProblem(UserProblem userProblem) {
    userProblemRepository.save(userProblem);
  }

  public List<UserProblem> getSolvedUserListByProblemId(Long problemId) {
    return userProblemRepository.findAllByProblemIdAndIsSolvedIsTrue(problemId);
  }

  // 시즌, 풀이 여부, 루틴에 따라 유저의 문제 목록을 가져옵니다.
  public List<UserProblem> getUserProblemList(Long userId, Integer season, Boolean isSolved) {
    System.out.println(
        userProblemRepository.findAllByUserIdAndSeasonAndIsSolvedOrderBySolvedAtDesc(
            userId, season, isSolved));
    System.out.println(season);
    System.out.println(isSolved);
    return userProblemRepository.findAllByUserIdAndSeasonAndIsSolvedOrderBySolvedAtDesc(
        userId, season, isSolved);
  }

  public Boolean isProblemSolvedToday(Long userId, Long id) {
    return userProblemRepository.existsByUserIdAndProblemIdAndSolvedAtAfter(
        userId, id, LocalDateTime.now().minusDays(1));
  }

  public int getSolvedUserCount(Long problemId) {
    return userProblemRepository.countSolvingUsersByProblemId(problemId, currentSeason);
  }
}
