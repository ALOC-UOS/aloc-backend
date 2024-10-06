package com.aloc.aloc.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aloc.aloc.scraper.DailyProblemAdditionStrategy;
import com.aloc.aloc.webhook.DiscordWebhookService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProblemCrawlingScheduler {

	private final DailyProblemAdditionStrategy dailyProblemAdditionStrategy;
	private final DiscordWebhookService discordWebhookService;

//	@Scheduled(cron = "0 55 23 * * TUE")	// 코스 변경 요청을 처리한 후, user problem을 할당합니다.
	public void scheduleAddProblemsForThisWeek() {
		try {
			String result = dailyProblemAdditionStrategy.addProblems();
			discordWebhookService.sendNotification(result);
		} catch (Exception e) {
			e.printStackTrace();
			discordWebhookService.sendNotification("크롤링 중 오류가 발생했습니다: " + e.getMessage());
		}
	}
}
