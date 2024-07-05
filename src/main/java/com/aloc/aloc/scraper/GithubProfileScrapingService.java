package com.aloc.aloc.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
public class GithubProfileScrapingService {

	public String extractProfileNumber(String githubId) {
		try {
			// HTML 파싱
			String url = buildUrl(githubId);
			Document doc = Jsoup.connect(url).get();

			// img 태그 찾기
			Element imgElement = doc.selectFirst("img.avatar.avatar-user.width-full.border.color-bg-default");
			if (imgElement != null) {
				String imgSrc = imgElement.attr("src");
				// 프로필 번호 추출
				return imgSrc.split("/")[4].split("\\?")[0];
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("잘못된 깃허브 정보입니다");
		}
		return null;
	}

	private String buildUrl(String githubId) {
		return "https://github.com/%s".formatted(githubId);
	}
}
