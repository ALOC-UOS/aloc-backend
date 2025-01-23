package com.aloc.aloc.history.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HistoryResponseDto {
  private String date;
  private List<HistoryContentDto> contents;
}
