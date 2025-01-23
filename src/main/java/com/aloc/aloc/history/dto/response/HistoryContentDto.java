package com.aloc.aloc.history.dto.response;

import com.aloc.aloc.history.History;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class HistoryContentDto {
  private String icon;
  private String name;
  private Integer rank;

  public static List<HistoryContentDto> listOf(List<History> histories) {
    return histories.stream()
        .map(
            history ->
                HistoryContentDto.builder()
                    .icon(history.getIcon())
                    .name(history.getUsername())
                    .rank(history.getRank())
                    .build())
        .collect(Collectors.toList());
  }
}
