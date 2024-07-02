package com.aloc.aloc.algorithm.enums;

import java.util.Arrays;
import java.util.List;

import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CourseRoutineTier {
	HALF_WEEKLY(Course.HALF, Routine.WEEKLY, Arrays.asList(5, 6, 7, 8, 9)),
	HALF_DAILY(Course.HALF, Routine.DAILY, Arrays.asList(7, 7, 7, 8, 9)),
	FULL_WEEKLY(Course.FULL, Routine.WEEKLY, Arrays.asList(9, 10, 11, 12, 13)),
	FULL_DAILY(Course.FULL, Routine.DAILY, Arrays.asList(11, 11, 11, 12, 12));

	private final Course course;
	private final Routine routine;
	private final List<Integer> tierList;
}
