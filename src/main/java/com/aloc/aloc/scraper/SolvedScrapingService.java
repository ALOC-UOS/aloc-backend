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
public class SolvedScrapingService {

	public boolean isProblemSolvedToday(String baekjoonId, Long problemId) throws IOException {
		// 스크래핑을 통해 백준에서 특정 문제를 오늘 풀었는지 확인합니다.
		String url = buildBaekjoonUrl(baekjoonId, problemId);
		String pageContent = fetchPageContent(url);
		LocalDate recentlySubmissionDate = parseRecentlySolvedDate(pageContent);
		return recentlySubmissionDate != null && recentlySubmissionDate.isEqual(LocalDate.now());
	}

	public boolean isProblemSolved(String baekjoonId, Problem problem) throws IOException {
		// 스크래핑을 통해 백준에서 특정 문제를 문제 공개 일시 이후에 풀었는지 확인합니다.
		String url = buildBaekjoonUrl(baekjoonId, problem.getId());
		String pageContent = fetchPageContent(url);
		LocalDate recentlySubmissionDate = parseRecentlySolvedDate(pageContent);
		LocalDate problemOpenedDate = LocalDate.from(problem.getUpdatedAt());
		return recentlySubmissionDate != null
			&& (recentlySubmissionDate.isAfter(problemOpenedDate) || recentlySubmissionDate.isEqual(problemOpenedDate));
	}

	private String buildBaekjoonUrl(String baekjoonId, Long problemId) {
		return String.format("https://www.acmicpc.net/status?problem_id=%d&user_id=%s&language_id=-1&result_id=4",
			problemId, baekjoonId);
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
			String recentlySubmissionDateStr = tdList.last().select("a").attr("title");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			return LocalDate.parse(recentlySubmissionDateStr, formatter);
		}
		return null;
	}
}
