package com.aloc.aloc.notion.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.aloc.aloc.notion.NotionProperty;
import com.aloc.aloc.notion.NotionProperty.NotionDatabase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotionClient {
	private final NotionProperty notionProperty;
	private final RestTemplate restTemplate;
	private String queryDatabase(String databaseId) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(notionProperty.getToken());
		headers.set("Notion-Version", "2022-06-28");
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>("{}", headers);

		String url = "https://api.notion.com/v1/databases/" + databaseId + "/query";
		return restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
	}

	public String queryDatabaseByName(String databaseName) {
		NotionDatabase database = notionProperty.getDatabases().get(databaseName);
		if (database == null) {
			throw new IllegalArgumentException(databaseName + "이라는 이름을 가진 노션 데이터베이스를 찾을 수 없습니다.");
		}
		return queryDatabase(database.getId());
	}
}
