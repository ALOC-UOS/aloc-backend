package com.aloc.aloc.problem.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class BaekjoonScrapingService {

	public boolean isProblemSolvedToday(String baekjoonId, Long problemId) throws IOException {
		// 스크래핑을 통해 백준에서 특정 문제를 풀었는지 확인합니다.
		String url = buildBaekjoonUrl(baekjoonId, problemId);
		String pageContent = fetchPageContent(url);
		return parseAndCheckSolvedToday(pageContent);
	}

	private String buildBaekjoonUrl(String baekjoonId, Long problemId) {
		return String.format("https://www.acmicpc.net/status?problem_id=%d&user_id=%s&language_id=-1&result_id=4",
			problemId, baekjoonId);
	}

	private String fetchPageContent(String url) throws IllegalArgumentException, IOException {
		return Jsoup.connect(url).get().html();
	}

	private boolean parseAndCheckSolvedToday(String pageContent) {
		Document doc = Jsoup.parse(pageContent);
		Element tbody = doc.select("tbody").first();

		// 채점 현황에서 내 제출을 불러온 후, 제출한 문제 중 가장 최근 것이 오늘인지 확인합니다.
		if (tbody != null && tbody.select("tr").first() != null) {
			Elements tdList = tbody.select("tr").first().select("td");
			String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			return tdList.last().select("a").attr("title").contains(todayDate);
		}
		return false;
	}

}
