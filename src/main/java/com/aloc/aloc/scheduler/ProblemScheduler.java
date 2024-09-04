package com.aloc.aloc.scheduler;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
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

	@Value("${app.vacation}")
	private boolean isVacation;

	@Scheduled(cron = "0 */13 * * * *")
	public void dailyScheduledTasks() {
		if (isWednesday()) {
			executeWednesdayTasks();
		} else {
			executeDailyTasks();
		}
	}

	private void executeWednesdayTasks() {
		problemFacade.updateAllUserProblem();
		if (isVacation) {
			updateProblemHidden(Routine.WEEKLY);
		}
	}

	private void executeDailyTasks() {
		updateProblemHidden(Routine.DAILY);
	}

	private void updateProblemHidden(Routine routine) {
		Integer openedCnt = problemService.updateProblemHiddenFalse(routine);
		discordWebhookService.sendNotification(openedCnt + "개의 " + routine.name() + " 문제가 공개되었습니다.");
	}

	private boolean isWednesday() {
		return LocalDate.now().getDayOfWeek().getValue() == 3;
	}
}
