package com.aloc.aloc.notion.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudyScheduleResponse {
	private int week;
	private String date;
	private String location;
}
