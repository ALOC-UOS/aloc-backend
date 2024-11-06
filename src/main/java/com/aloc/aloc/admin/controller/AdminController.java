package com.aloc.aloc.admin.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aloc.aloc.scheduler.ProblemCrawlingScheduler;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api2/admin")
@Tag(name = "Admin API", description = "관리자용 API 입니다.")

public class AdminController {
	private final ProblemCrawlingScheduler problemCrawlingScheduler;
	@PostMapping("/crawl")
	public void crawl() {
		problemCrawlingScheduler.scheduleAddProblemsForThisWeek();
	}
}
