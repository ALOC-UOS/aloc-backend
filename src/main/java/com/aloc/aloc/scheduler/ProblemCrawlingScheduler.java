package com.aloc.aloc.scheduler;

import java.io.IOException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aloc.aloc.scraper.ProblemScraperService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProblemCrawlingScheduler {

	private final ProblemScraperService problemScraperService;

	@Scheduled(cron = "0 0 0 * * MON")
	public void scheduleAddProblemsForThisWeek() {
		try {
			problemScraperService.addProblemsForThisWeek();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
