package com.aloc.aloc.problem.entity;

import com.aloc.aloc.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProblem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "problem_id")
  private Problem problem;

  private Integer season;

  @Builder.Default private Boolean isSolved = false;

  private LocalDateTime solvedAt;

  public void setIsSolved(boolean isSolved) {
    this.isSolved = isSolved;
    if (isSolved) {
      this.solvedAt = LocalDateTime.now();
    } else {
      this.solvedAt = null;
    }
  }
}
