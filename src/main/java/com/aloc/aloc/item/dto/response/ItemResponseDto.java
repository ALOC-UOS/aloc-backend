package com.aloc.aloc.item.dto.response;

import com.aloc.aloc.item.entity.Item;
import com.aloc.aloc.item.enums.ItemLocation;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemResponseDto {
  @Schema(description = "아이템 id", example = "1")
  private Long id;

  @Schema(description = "아이템 이름", example = "초록 거북이")
  private String name;

  @Schema(description = "아이템 설명", example = "귀여운 거북이가 엉금엉금 기어오릅니다.")
  private String description;

  @Schema(description = "아이템 가격", example = "1000")
  private Integer coin;

  @Schema(description = "아이템 타입(부위)", example = "TOP")
  private ItemLocation itemLocation;

  @Schema(description = "아이템 이미지 파일 이름 리스트", example = "turtle1.png")
  private List<String> imageFileNames;

  public static ItemResponseDto of(Item item, List<String> imageFileNames) {
    return ItemResponseDto.builder()
        .id(item.getId())
        .name(item.getName())
        .description(item.getDescription())
        .coin(item.getCoin())
        .itemLocation(item.getItemLocation())
        .imageFileNames(imageFileNames)
        .build();
  }
}
