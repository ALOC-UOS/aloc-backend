package com.aloc.aloc.user.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.aloc.aloc.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

	private String username;
	private String githubId;
	private String baekjoonId;
	private String profileColor;
	private String studentId;
	private String profileNumber;
	private Integer rank;
	private Integer coin;

	public static UserResponseDto of(User user) {
		return UserResponseDto.builder()
			.username(user.getUsername())
			.githubId(user.getGithubId())
			.baekjoonId(user.getBaekjoonId())
			.profileColor(user.getProfileColor())
			.studentId(user.getStudentId())
			.profileNumber(user.getProfileNumber())
			.rank(user.getRank())
			.coin(user.getCoin())
			.build();
	}

	public static List<UserResponseDto> listOf(List<User> users) {
		return users.stream().map(UserResponseDto::of).collect(Collectors.toList());
	}
}
