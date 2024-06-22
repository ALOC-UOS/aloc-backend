package com.aloc.aloc.domain.user.dto.response;

import com.aloc.aloc.domain.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

	private String username;
	private String githubId;
	private String baekjoonId;
	private String profileColor;
	private String studentId;
	private Integer profileNumber;
	private Integer rank;
	private Integer coin;
	public static UserResponseDto of(User user) {
		return new UserResponseDto(
			user.getUsername(),
			user.getGithubId(),
			user.getBaekjoonId(),
			user.getProfileColor(),
			user.getStudentId(),
			user.getProfileNumber(),
			user.getRank(),
			user.getCoin()
		);
	}
}
