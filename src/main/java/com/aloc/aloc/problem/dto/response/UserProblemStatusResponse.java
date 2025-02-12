package com.aloc.aloc.problem.dto.response;

import com.aloc.aloc.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserProblemStatusResponse {
  private String username;
  private String githubId;
  private String baekjoonId;
  private Integer solvedCount;
  private Integer unsolvedCount;
  private Boolean todaySolved;

  public static UserProblemStatusResponse of(
      User user, Integer unsolvedCount, Boolean todaySolved) {
    return UserProblemStatusResponse.builder()
        .username(user.getUsername())
        .githubId(user.getGithubId())
        .solvedCount(user.getSolvedCount())
        .unsolvedCount(unsolvedCount)
        .todaySolved(todaySolved)
        .build();
  }
}
