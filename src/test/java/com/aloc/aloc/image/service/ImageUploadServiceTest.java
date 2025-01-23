package com.aloc.aloc.image.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aloc.aloc.image.dto.UploadedImageInfo;
import com.aloc.aloc.image.enums.ImageType;
import com.aloc.aloc.image.service.strategy.FileStorageStrategy;
import com.aloc.aloc.image.service.strategy.ImageUploadStrategy;
import com.aloc.aloc.image.service.strategy.ItemImageUploadStrategy;
import com.aloc.aloc.image.service.strategy.ProfileImageUploadStrategy;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

public class ImageUploadServiceTest {
  @Mock private ProfileImageUploadStrategy profileStrategy;

  @Mock private ItemImageUploadStrategy itemStrategy;

  @Mock private FileStorageStrategy fileStorageStrategy;

  private ImageUploadService imageUploadService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    List<ImageUploadStrategy> strategyList = List.of(profileStrategy, itemStrategy);
    imageUploadService = new ImageUploadService(strategyList);
  }

  @Test
  @DisplayName("Test upload profile image success")
  void testUploadProfileImageSuccess() throws Exception {
    MultipartFile mockFile = mock(MultipartFile.class);
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("username", "testUser");
    UploadedImageInfo mockFileInfo =
        new UploadedImageInfo(ImageType.PROFILE, "testFileName.jpg", Path.of("url/to/file"));

    when(profileStrategy.upload(mockFile, metadata)).thenReturn(mockFileInfo);

    UploadedImageInfo result =
        imageUploadService.uploadImage(mockFile, ImageType.PROFILE, metadata);

    assertNotNull(result);
    assertEquals("testFileName.jpg", result.getImageName());
    verify(profileStrategy).upload(mockFile, metadata);
  }

  @Test
  @DisplayName("Test upload item image success")
  void testUploadItemImageSuccess() throws Exception {
    MultipartFile mockFile = mock(MultipartFile.class);
    Map<String, Object> metadata = new HashMap<>();
    UploadedImageInfo mockFileInfo =
        new UploadedImageInfo(ImageType.ITEM, "testFileName.jpg", Path.of("url/to/file"));

    when(itemStrategy.upload(mockFile, metadata)).thenReturn(mockFileInfo);

    UploadedImageInfo result = imageUploadService.uploadImage(mockFile, ImageType.ITEM, metadata);

    assertNotNull(result);
    assertEquals("testFileName.jpg", result.getImageName());
    verify(itemStrategy).upload(mockFile, metadata);
  }

  @Test
  @DisplayName("Test upload image with null image type")
  void testUnsupportedImageType() {
    MultipartFile mockFile = mock(MultipartFile.class);
    Map<String, Object> metadata = new HashMap<>();

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              imageUploadService.uploadImage(mockFile, null, metadata); // null로 검증
            });

    assertEquals("Unsupported image type: null", exception.getMessage());
  }

  @Test
  void testMissingMetadata() {
    MultipartFile mockFile = mock(MultipartFile.class);
    Map<String, Object> metadata = new HashMap<>();

    try {
      when(profileStrategy.upload(any(MultipartFile.class), anyMap()))
          .thenThrow(new FileUploadException("User ID is required for profile image upload"));
      profileStrategy.upload(mockFile, metadata);
      fail("Expected IllegalArgumentException to be thrown");
    } catch (FileUploadException e) {
      assertEquals("User ID is required for profile image upload", e.getMessage());
    }
  }
}
