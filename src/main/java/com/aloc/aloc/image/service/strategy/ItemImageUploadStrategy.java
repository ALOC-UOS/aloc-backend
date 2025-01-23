package com.aloc.aloc.image.service.strategy;

import com.aloc.aloc.image.dto.UploadedImageInfo;
import com.aloc.aloc.image.enums.ImageType;
import com.aloc.aloc.image.util.FileNameGenerator;
import com.aloc.aloc.image.util.ImageTypePathResolver;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class ItemImageUploadStrategy implements ImageUploadStrategy {
  private final FileStorageStrategy fileStorageStrategy;
  private final ImageTypePathResolver pathResolver;

  @Override
  public UploadedImageInfo upload(MultipartFile file, Map<String, Object> metadata)
      throws FileUploadException {
    String fileName = FileNameGenerator.generateUniqueFileName(file.getOriginalFilename());
    Path uploadPath = pathResolver.resolvePath(ImageType.ITEM);
    try {
      Path fullPath = fileStorageStrategy.storeFile(file, uploadPath, fileName);
      return new UploadedImageInfo(ImageType.ITEM, fileName, fullPath);
    } catch (IOException e) {
      throw new FileUploadException("Failed to upload item image", e);
    }
  }
}
