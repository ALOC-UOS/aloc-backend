package com.aloc.aloc.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aloc.aloc.problem.service.ProblemFacade;
import com.aloc.aloc.problem.service.ProblemService;
import com.aloc.aloc.problemtype.enums.Routine;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProblemScheduler {
	private final ProblemFacade problemFacade;
	private final ProblemService problemService;

	// weekly 문제를 공개합니다.
	public void updateAllWeeklyProblemHidden() {
		problemService.updateProblemHiddenFalse(Routine.WEEKLY);
		updateDailyProblemHidden();
	}

	// 수요일 부터 일요일까지 매일 daily 문제를 공개합니다.
	@Scheduled(cron = "0 0 0 * * WED,THU,FRI,SAT,SUN,MON")
	public void updateDailyProblemHidden() {
		problemService.updateProblemHiddenFalse(Routine.DAILY);
	}

	@Scheduled(cron = "0 0 0 * * TUE")
	// 코스 변경 요청을 처리한 후, user problem을 할당합니다.
	public void updateAllUserProblem() {
		problemFacade.updateAllUserProblem();
		updateAllWeeklyProblemHidden();
	}
}
