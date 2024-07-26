package com.aloc.aloc.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aloc.aloc.scraper.ProblemScrapingService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProblemCrawlingScheduler {

	private final ProblemScrapingService problemScraperService;

	@Scheduled(cron = "0 0 0 * * MON")
	// 매주 월요일에 문제를 크롤링해옵니다.
	public void scheduleAddProblemsForThisWeek() {
		try {
			problemScraperService.addProblemsForThisWeek();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
