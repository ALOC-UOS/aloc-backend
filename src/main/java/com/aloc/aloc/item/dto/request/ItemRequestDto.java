package com.aloc.aloc.item.dto.request;

import com.aloc.aloc.item.enums.ItemLocation;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ItemRequestDto {
  @Schema(description = "아이템 이름", example = "초록 거북이")
  private String name;

  @Schema(description = "아이템 설명", example = "귀여운 거북이가 엉금엉금 기어오릅니다.")
  private String description;

  @Schema(description = "아이템 가격", example = "1000")
  private Integer coin;

  @Schema(description = "아이템 타입(부위)", example = "TOP")
  private ItemLocation itemLocation;

  @Schema(description = "비공개 여부", example = "true")
  private Boolean isHidden;

  @Schema(description = "이미지 파일 리스트")
  private List<MultipartFile> images;
}
