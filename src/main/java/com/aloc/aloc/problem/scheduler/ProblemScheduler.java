package com.aloc.aloc.problem.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aloc.aloc.problem.service.ProblemFacade;
import com.aloc.aloc.problemtype.enums.Routine;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProblemScheduler {
	private final ProblemFacade problemFacade;

	@Scheduled(cron = "0 0 0 * * TUE")
	public void updateAllWeeklyProblemHidden() {
		problemFacade.updateProblemHiddenFalse(Routine.WEEKLY);
		updateDailyProblemHidden();
	}

	@Scheduled(cron = "0 0 0 * * WED-SAT")
	public void updateDailyProblemHidden() {
		problemFacade.updateProblemHiddenFalse(Routine.DAILY);
	}

}
