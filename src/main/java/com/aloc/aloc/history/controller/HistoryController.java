package com.aloc.aloc.history.controller;

import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.history.dto.response.HistoryResponseDto;
import com.aloc.aloc.history.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api2/history")
@Tag(name = "History API", description = "History API 입니다.")
public class HistoryController {

  private final HistoryService historyService;

  @GetMapping()
  @Operation(summary = "히스토리 목록 조회", description = "최근 추가된 순서로 모든 히스토리 목록 조회합니다.")
  public CustomApiResponse<List<HistoryResponseDto>> getHistories() {
    return CustomApiResponse.onSuccess(historyService.getHistories());
  }
}
