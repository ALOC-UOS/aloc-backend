package com.aloc.aloc.image.service.strategy;

import com.aloc.aloc.image.dto.UploadedImageInfo;
import java.util.Map;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadStrategy {
  UploadedImageInfo upload(MultipartFile file, Map<String, Object> metadata)
      throws FileUploadException;
}
