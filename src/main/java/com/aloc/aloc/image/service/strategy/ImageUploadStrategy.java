package com.aloc.aloc.image.service.strategy;

import java.util.Map;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import com.aloc.aloc.image.dto.UploadedFileInfo;

public interface ImageUploadStrategy {
	UploadedFileInfo upload(MultipartFile file, Map<String, Object> metadata) throws FileUploadException;
}
