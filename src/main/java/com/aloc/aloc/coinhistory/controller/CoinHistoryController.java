package com.aloc.aloc.coinhistory.controller;

import com.aloc.aloc.coinhistory.dto.response.CoinHistoryResponseDto;
import com.aloc.aloc.coinhistory.service.CoinHistoryService;
import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api2/coin-history")
@Tag(name = "CoinHistory API", description = "CoinHistory API 입니다.")
public class CoinHistoryController {
  private final CoinHistoryService coinHistoryService;
  private final UserService userService;

  @GetMapping("/{githubId}")
  @Operation(summary = "유저 코인 히스토리 조회", description = "유저의 코인 히스토리 목록을 조회합니다.")
  public CustomApiResponse<CoinHistoryResponseDto> getCoinHistory(
      @Parameter(description = "조회하려는 유저의 githubId", required = true) @PathVariable()
          String githubId) {
    return CustomApiResponse.onSuccess(
        coinHistoryService.getUserCoinHistory(userService.findUser(githubId)));
  }
}
