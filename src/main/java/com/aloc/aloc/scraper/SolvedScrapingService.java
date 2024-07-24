package com.aloc.aloc.scraper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.aloc.aloc.problem.entity.Problem;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
// TODO: SolvedCheckingService
public class SolvedScrapingService {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final String BASE_URL = "https://www.acmicpc.net/status?problem_id=%d&user_id=%s&language_id=-1&result_id=4";

	public boolean isProblemSolvedToday(String baekjoonId, Long problemId) throws IOException {
		return isSolvedToday(getRecentlySolvedDate(baekjoonId, problemId));
	}

	public boolean isProblemSolved(String baekjoonId, Problem problem) throws IOException {
		return isSolvedBefore(getRecentlySolvedDate(baekjoonId, problem.getId()),
			LocalDate.from(problem.getUpdatedAt()));
	}

	private boolean isSolvedBefore(LocalDate solvedDate, LocalDate openedDate) {
		return solvedDate != null && (solvedDate.isAfter(openedDate) || solvedDate.isEqual(openedDate));
	}

	private boolean isSolvedToday(LocalDate solvedDate) {
		return solvedDate != null && solvedDate.isEqual(LocalDate.now());
	}

	private LocalDate getRecentlySolvedDate(String baekjoonId, Long problemId) throws IOException {
		return parseRecentlySolvedDate(fetchPageContent(buildBaekjoonUrl(baekjoonId, problemId)));
	}

	private String buildBaekjoonUrl(String baekjoonId, Long problemId) {
		return String.format(BASE_URL, problemId, baekjoonId);
	}

	private String fetchPageContent(String url) throws IllegalArgumentException, IOException {
		return Jsoup.connect(url).get().html();
	}

	private LocalDate parseRecentlySolvedDate(String pageContent) {
		Document doc = Jsoup.parse(pageContent);
		Element tbody = doc.select("tbody").first();

		// 채점 현황에서 내 제출을 불러온 후, 제출한 문제 중 가장 최근 것이 오늘인지 확인합니다.
		if (tbody != null && tbody.select("tr").first() != null) {
			Elements tdList = tbody.select("tr").first().select("td");
			String recentlySolvedDateStr = tdList.last().select("a").attr("title");
			return LocalDate.parse(recentlySolvedDateStr, FORMATTER);
		}
		return null;
	}
}
