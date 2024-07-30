package com.aloc.aloc.notion.service;


import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.aloc.aloc.notion.dto.response.StudyScheduleResponseDto;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotionService {
	private final NotionClient notionClient;
	private static final String STUDY_SCHEDULE = "study-schedule";
	public StudyScheduleResponseDto getStudySchedule() {
		String response = notionClient.queryDatabaseByName(STUDY_SCHEDULE);
		JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
		JsonArray results = jsonObject.getAsJsonArray("results");
		JsonObject lastRow = results.get(0).getAsJsonObject(); // 가장 최근에 생성된 것을 불러옴
		JsonObject properties = lastRow.getAsJsonObject("properties");

		String date = properties.getAsJsonObject("모임일자").get("date")
			.getAsJsonObject().get("start").getAsString();
		String location = properties.getAsJsonObject("장소").get("rich_text")
			.getAsJsonArray().get(0).getAsJsonObject().get("text").getAsJsonObject().get("content").getAsString();
		String weekStr = properties.getAsJsonObject("주차").get("title")
			.getAsJsonArray().get(0).getAsJsonObject().get("text").getAsJsonObject().get("content").getAsString();

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
		OffsetDateTime offsetDateTime = OffsetDateTime
			.parse(dateTimeString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일 E요일, a h시", Locale.KOREAN);
		return offsetDateTime.format(formatter);
	}
}
