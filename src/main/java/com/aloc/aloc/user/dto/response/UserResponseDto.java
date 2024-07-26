package com.aloc.aloc.user.dto.response;

import com.aloc.aloc.user.User;
import com.aloc.aloc.user.enums.Authority;

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
	private Authority authority;
	private Integer rank;
	private Integer coin;

	public static UserResponseDto of(User user) {
		return UserResponseDto.builder()
			.username(user.getUsername())
			.authority(user.getAuthority())
			.githubId(user.getGithubId())
			.baekjoonId(user.getBaekjoonId())
			.profileColor(user.getProfileColor())
			.studentId(user.getStudentId())
			.profileNumber(user.getProfileNumber())
			.rank(user.getRank())
			.coin(user.getCoin())
			.build();
	}
}
