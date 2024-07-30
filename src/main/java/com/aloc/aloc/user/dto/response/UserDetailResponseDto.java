package com.aloc.aloc.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

import com.aloc.aloc.user.User;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class UserDetailResponseDto extends UserResponseDto {
	@Schema(description = "해결한 문제 수", example = "3")
	private final Integer solvedCount;
	@Schema(description = "해결하지 못한 문제 수", example = "2")
	private final Integer unsolvedCount;
	@Schema(description = "오늘의 daily 문제 해결 여부", example = "true")
	private final Boolean todaySolved;
	@Schema(description = "색상 분류", example = "special")
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
			.authority(user.getAuthority())
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
