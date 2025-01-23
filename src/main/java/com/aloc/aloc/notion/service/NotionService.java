package com.aloc.aloc.notion.service;

import com.aloc.aloc.notion.dto.response.StudyScheduleResponseDto;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotionService {
  private final NotionClient notionClient;
  private static final String STUDY_SCHEDULE = "study-schedule";

  public StudyScheduleResponseDto getStudySchedule() {
    JsonArray results = notionClient.queryDatabaseByName(STUDY_SCHEDULE);

    JsonObject lastRow =
        StreamSupport.stream(results.spliterator(), false)
            .map(JsonObject.class::cast)
            .max(Comparator.comparing(jsonObject -> jsonObject.get("created_time").getAsString()))
            .orElseThrow(() -> new IllegalStateException("No data found in Notion database"));

    JsonObject properties = lastRow.getAsJsonObject("properties");

    String date =
        properties.getAsJsonObject("모임일자").get("date").getAsJsonObject().get("start").getAsString();
    String location =
        properties
            .getAsJsonObject("장소")
            .get("rich_text")
            .getAsJsonArray()
            .get(0)
            .getAsJsonObject()
            .get("text")
            .getAsJsonObject()
            .get("content")
            .getAsString();
    String weekStr =
        properties
            .getAsJsonObject("주차")
            .get("title")
            .getAsJsonArray()
            .get(0)
            .getAsJsonObject()
            .get("text")
            .getAsJsonObject()
            .get("content")
            .getAsString();

    return StudyScheduleResponseDto.builder()
        .week(extractWeekNumber(weekStr))
        .date(formatDateTime(date))
        .location(location)
        .build();
  }

  private int extractWeekNumber(String weekStr) {
    return Integer.parseInt(weekStr.replaceAll("[^0-9]", ""));
  }

  private String formatDateTime(String dateTimeString) {
    OffsetDateTime offsetDateTime =
        OffsetDateTime.parse(dateTimeString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일 E요일, a h시", Locale.KOREAN);
    return offsetDateTime.format(formatter);
  }
}
