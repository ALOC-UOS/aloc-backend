package com.aloc.aloc.problem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemRequestDto {
  @Schema(description = "문제 백준 ID", example = "1080")
  private Integer problemId;

  @Schema(description = "문제 제목", example = "A와 B ")
  private String title;

  @Schema(description = "알고리즘 이름", example = "알고리즘 이름")
  private String algorithm;

  @Schema(description = "문제 타입 아이디", example = "1(FULL_DAILY) or 2(FULL_WEEKLY)")
  private Long problemType;
}
