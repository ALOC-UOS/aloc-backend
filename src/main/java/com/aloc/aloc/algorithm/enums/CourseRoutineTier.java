package com.aloc.aloc.algorithm.enums;

import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// TODO: 나중에 백오피스로 관리할 수 있도록 분리하기
public enum CourseRoutineTier {
  HALF_WEEKLY(Course.HALF, Routine.WEEKLY, Arrays.asList(5, 6, 7, 8, 9), 5),
  HALF_DAILY(Course.HALF, Routine.DAILY, Arrays.asList(7, 8, 9), 7),
  FULL_WEEKLY(Course.FULL, Routine.WEEKLY, Arrays.asList(9, 10, 11, 12, 13), 5),
  FULL_DAILY(Course.FULL, Routine.DAILY, Arrays.asList(10, 11, 12), 7);

  private final Course course;
  private final Routine routine;
  private final List<Integer> tierList;
  private final Integer targetCount;
}
