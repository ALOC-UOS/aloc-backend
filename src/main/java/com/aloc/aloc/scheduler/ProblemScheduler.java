package com.aloc.aloc.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aloc.aloc.problem.service.ProblemFacade;
import com.aloc.aloc.problem.service.ProblemService;
import com.aloc.aloc.problemtype.enums.Routine;
import com.aloc.aloc.webhook.DiscordWebhookService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProblemScheduler {
	private final ProblemFacade problemFacade;
	private final ProblemService problemService;
	private final DiscordWebhookService discordWebhookService;

	// weekly 문제를 공개합니다.
	public void updateAllWeeklyProblemHidden() {
		Integer openedCnt = problemService.updateProblemHiddenFalse(Routine.WEEKLY);
		discordWebhookService.sendNotification(openedCnt + "개의 Weekly 문제가 공개되었습니다.");
		updateDailyProblemHidden();
	}

	// 수요일 부터 일요일까지 매일 daily 문제를 공개합니다.
	@Scheduled(cron = "0 0 0 * * WED,THU,FRI,SAT,SUN,MON")
	public void updateDailyProblemHidden() {
		Integer openedCnt = problemService.updateProblemHiddenFalse(Routine.DAILY);
		discordWebhookService.sendNotification(openedCnt + "개의 Daily 문제가 공개되었습니다.");
	}

//	@Scheduled(cron = "0 0 0 * * TUE")
	@Scheduled(cron = "0 5 10 ? * TUE")	// 코스 변경 요청을 처리한 후, user problem을 할당합니다.
	public void updateAllUserProblem() {
		problemFacade.updateAllUserProblem();
		updateAllWeeklyProblemHidden();
	}
}
