package com.aloc.aloc.image.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import com.aloc.aloc.image.dto.UploadedFileInfo;
import com.aloc.aloc.image.enums.ImageType;
import com.aloc.aloc.image.service.ImageUploadService;

@WebMvcTest(ImageController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class ImageControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private JpaMetamodelMappingContext jpaMetamodelMappingContext;

	@MockBean
	private ImageUploadService imageUploadService;
	private UploadedFileInfo uploadedFileInfo;

	@BeforeEach
	public void setup() {
		uploadedFileInfo = new UploadedFileInfo(
			ImageType.ITEM,
			"uploadedFileName.jpg",
			Path.of("/uploads/uploadedFileName.jpg")
		);
	}

	@Test
	public void testUploadItemImage() throws Exception {
		Mockito.when(imageUploadService.uploadImage(any(MultipartFile.class), eq(ImageType.ITEM), isNull()))
			.thenReturn(uploadedFileInfo);

		mockMvc.perform(multipart("/api2/images/upload/items")
				.file("file", "dummy content".getBytes())
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isOk())
			.andExpect(content().json(
				"{\"message\":\"Item File uploaded successfully\","
					+ "\"fileName\":\"uploadedFileName.jpg\"}")
			);
	}

	@Test
	@WithMockUser(username = "testuser")
	public void testUploadProfileImage() throws Exception {
		Mockito.when(imageUploadService.uploadImage(any(MultipartFile.class), eq(ImageType.PROFILE),
				anyMap()))
			.thenReturn(uploadedFileInfo);

		mockMvc.perform(multipart("/api2/images/upload/profile")
				.file("file", "dummy content".getBytes())
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isOk())
			.andExpect(content().json(
				"{\"message\":\"Profile File uploaded successfully\",\"fileName\":\"uploadedFileName.jpg\"}"));
	}
}
