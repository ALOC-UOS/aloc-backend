package com.aloc.aloc.image.service.strategy;

import com.aloc.aloc.image.dto.UploadedImageInfo;
import com.aloc.aloc.image.enums.ImageType;
import com.aloc.aloc.image.util.FileNameGenerator;
import com.aloc.aloc.image.util.ImageTypePathResolver;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.service.UserService;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class ProfileImageUploadStrategy implements ImageUploadStrategy {
  private final FileStorageStrategy fileStorageStrategy;
  private final ImageTypePathResolver pathResolver;
  private final UserService userService;

  @Override
  public UploadedImageInfo upload(MultipartFile file, Map<String, Object> metadata)
      throws FileUploadException {
    String userId = (String) metadata.get("username");
    if (userId == null) {
      throw new IllegalArgumentException("User ID is required for profile image upload");
    }

    String fileName = FileNameGenerator.generateUniqueFileName(file.getOriginalFilename());
    Path uploadPath = pathResolver.resolvePath(ImageType.PROFILE);
    try {
      Path fullPath = fileStorageStrategy.storeFile(file, uploadPath, fileName);
      User user = userService.findUser(userId);
      user.setProfileImageFileName(fileName);
      userService.saveUser(user);

      return new UploadedImageInfo(ImageType.PROFILE, fileName, fullPath);
    } catch (IOException e) {
      throw new FileUploadException("Failed to upload profile image", e);
    }
  }
}
