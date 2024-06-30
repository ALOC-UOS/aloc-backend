package com.aloc.aloc.domain.user.dto.request;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.aloc.aloc.domain.user.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class UserRequestDto {

	@Schema(description = "사용자 이름", example = "홍길동")
	private String username;

	@Schema(description = "비밀번호", example = "password")
	private String password;

	@Schema(description = "깃허브 아이디", example = "githubId")
	private String githubId;

	@Schema(description = "백준 아이디", example = "baekjoonId")
	private String baekjoonId;

	@Schema(description = "학번", example = "2020920000")
	private String studentId;

	@Schema(description = "디스코드 아이디", example = "discordId")
	private String discordId;

	@Schema(description = "노션 초대 이메일", example = "notion@uos.ac.kr")
	private String notionEmail;

	public User toEntity(BCryptPasswordEncoder passwordEncoder) {
		return User.builder()
			.username(username)
			.password(passwordEncoder.encode(password))
			.githubId(githubId)
			.baekjoonId(baekjoonId)
			.studentId(studentId)
			.discordId(discordId)
			.notionEmail(notionEmail)
			.build();
	}
}
