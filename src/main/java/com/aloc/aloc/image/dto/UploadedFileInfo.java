package com.aloc.aloc.image.dto;

import com.aloc.aloc.image.enums.ImageType;
import java.nio.file.Path;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadedFileInfo {
  private ImageType imageType;
  private String fileName;
  private Path fullPath;

  public String getRelativePath() {
    return imageType.name().toLowerCase() + "/" + fileName;
  }
}
