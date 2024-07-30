package com.aloc.aloc.notion.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.notion.dto.response.StudyScheduleResponse;
import com.aloc.aloc.notion.service.NotionService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Notion API", description = "Notion API 입니다.")
@RequestMapping("/api2/notion")
public class NotionController {
	private final NotionService notionService;

	@GetMapping("/study-schedule")
	@Operation(summary = "스터디 일정 조회", description = "예정된 스터디 일정 및 장소를 조회합니다.")
	public CustomApiResponse<StudyScheduleResponse> getStudySchedule() {
		return CustomApiResponse.onSuccess(notionService.getStudySchedule());
	}

}
