package com.aloc.aloc.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aloc.aloc.scraper.ProblemScrapingService;
import com.aloc.aloc.webhook.DiscordWebhookService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProblemCrawlingScheduler {

	private final ProblemScrapingService problemScraperService;
	private final DiscordWebhookService discordWebhookService;

	@Scheduled(cron = "0 */3 * * * MON")
	// 매주 월요일에 문제를 크롤링해옵니다.
	public void scheduleAddProblemsForThisWeek() {
		try {
			String result = problemScraperService.addProblemsForThisWeek();
			discordWebhookService.sendNotification(result);
		} catch (Exception e) {
			e.printStackTrace();
			discordWebhookService.sendNotification("크롤링 중 오류가 발생했습니다: " + e.getMessage());
		}
	}
}
