package com.aloc.aloc.algorithm.scheduler;

import java.io.IOException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aloc.aloc.algorithm.service.CrawlingService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AlgorithmScheduler {

	private final CrawlingService crawlingService;

	@Scheduled(cron = "0 0 0 * * MON")
	public void scheduleAddProblemsForThisWeek() {
		try {
			crawlingService.addProblemsForThisWeek();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
