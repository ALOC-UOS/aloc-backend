package com.aloc.aloc.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aloc.aloc.notification.DiscordNotificationService;
import com.aloc.aloc.scraper.ProblemScrapingService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProblemCrawlingScheduler {

	private final ProblemScrapingService problemScraperService;
	private final DiscordNotificationService discordNotificationService;

	@Scheduled(cron = "0 0 0 * * MON")
	public void scheduleAddProblemsForThisWeek() {
		try {
			String result = problemScraperService.addProblemsForThisWeek();
			discordNotificationService.sendNotification(result);
		} catch (Exception e) {
			e.printStackTrace();
			discordNotificationService.sendNotification("크롤링 중 오류가 발생했습니다: " + e.getMessage());
		}
	}
}
