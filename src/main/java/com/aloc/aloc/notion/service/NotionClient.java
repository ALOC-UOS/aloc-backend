package com.aloc.aloc.notion.service;

import com.aloc.aloc.notion.NotionProperty;
import com.aloc.aloc.notion.NotionProperty.NotionDatabase;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class NotionClient {
  private final NotionProperty notionProperty;
  private final RestTemplate restTemplate;
  private static final String NOTION_DATABASE_URL = "https://api.notion.com/v1/databases/%s/query";

  private JsonArray queryDatabase(String databaseId) {
    HttpHeaders headers = initHeader();
    HttpEntity<String> entity = new HttpEntity<>("{}", headers);

    String url = buildNotionDatabaseUrl(databaseId);
    String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();

    JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
    return jsonObject.getAsJsonArray("results");
  }

  private HttpHeaders initHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(notionProperty.getToken());
    headers.set("Notion-Version", "2022-06-28");
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  private String buildNotionDatabaseUrl(String databaseId) {
    return String.format(NOTION_DATABASE_URL, databaseId);
  }

  public JsonArray queryDatabaseByName(String databaseName) {
    NotionDatabase database = notionProperty.getDatabases().get(databaseName);
    if (database == null) {
      throw new IllegalArgumentException(databaseName + "이라는 이름을 가진 노션 데이터베이스를 찾을 수 없습니다.");
    }
    return queryDatabase(database.getId());
  }
}
