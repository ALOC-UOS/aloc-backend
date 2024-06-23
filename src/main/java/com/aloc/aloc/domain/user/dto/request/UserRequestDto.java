package com.aloc.aloc.domain.user.dto.request;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.aloc.aloc.domain.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

	private String username;

	private String password;

	private String githubId;

	private String baekjoonId;

	private String studentId;

	public User toEntity(BCryptPasswordEncoder passwordEncoder) {
		return User.builder()
			.username(username)
			.password(passwordEncoder.encode(password))
			.githubId(githubId)
			.baekjoonId(baekjoonId)
			.studentId(studentId)
			.build();
	}
}
