package com.aloc.aloc.user.dto.response;

import java.time.LocalDateTime;

import com.aloc.aloc.user.User;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class UserDetailResponseDto extends UserResponseDto {
	private final Integer solvedCount;
	private final Integer unsolvedCount;
	private final Boolean todaySolved;
	private final String colorCategory;
	private final String color1;
	private final String color2;
	private final String color3;
	private final String color4;
	private final String color5;
	private final Integer degree;
	private final LocalDateTime createdAt;

	public static UserDetailResponseDto of(User user,
		Integer unsolvedCount,
		Boolean todaySolved,
		String colorCategory,
		String color1,
		String color2,
		String color3,
		String color4,
		String color5,
		Integer degree) {
		return UserDetailResponseDto.builder()
			.username(user.getUsername())
			.githubId(user.getGithubId())
			.baekjoonId(user.getBaekjoonId())
			.profileColor(user.getProfileColor())
			.studentId(user.getStudentId())
			.profileNumber(user.getProfileNumber())
			.rank(user.getRank())
			.coin(user.getCoin())
			.solvedCount(user.getSolvedCount())
			.unsolvedCount(unsolvedCount)
			.todaySolved(todaySolved)
			.colorCategory(colorCategory)
			.color1(color1)
			.color2(color2)
			.color3(color3)
			.color4(color4)
			.color5(color5)
			.degree(degree)
			.createdAt(user.getCreatedAt())
			.build();
	}
}
