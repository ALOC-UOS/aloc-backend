package com.aloc.aloc.notion.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudyScheduleResponseDto {
  @Schema(description = "주차", example = "1")
  private int week;

  @Schema(description = "스터디 날짜 및 시간", example = "7월 30일 화요일, 오후 7시")
  private String date;

  @Schema(description = "스터디 장소", example = "정보기술관 110/111호")
  private String location;
}
