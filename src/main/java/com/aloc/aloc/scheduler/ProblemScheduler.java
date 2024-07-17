package com.aloc.aloc.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aloc.aloc.problem.service.ProblemService;
import com.aloc.aloc.problemtype.enums.Routine;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProblemScheduler {
	private final ProblemService problemService;

	@Scheduled(cron = "0 0 0 * * TUE")
	public void updateAllWeeklyProblemHidden() {
		problemService.updateProblemHiddenFalse(Routine.WEEKLY);
		updateDailyProblemHidden();
	}

	@Scheduled(cron = "0 0 0 * * WED-SAT")
	public void updateDailyProblemHidden() {
		problemService.updateProblemHiddenFalse(Routine.DAILY);
	}

}
