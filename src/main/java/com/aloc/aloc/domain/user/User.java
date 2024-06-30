package com.aloc.aloc.domain.user;


import org.springframework.security.crypto.password.PasswordEncoder;

import com.aloc.aloc.domain.problemtype.enums.Course;
import com.aloc.aloc.domain.user.enums.Authority;
import com.aloc.aloc.global.domain.AuditingTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "study_user")
public class User extends AuditingTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String baekjoonId;

	@Column(nullable = false)
	private String githubId;

	@Column(nullable = false)
	private String studentId;

	private String discordId;

	private String notionEmail;

	private Integer profileNumber;

	private Integer rank;

	private Integer coin;

	@Enumerated(EnumType.STRING)
	private Course course;

	@Column(nullable = false)
	private String profileColor;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	private Authority authority;

	@Column(length = 1000)
	private String refreshToken;


	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void destroyRefreshToken() {
		this.refreshToken = null;
	}

	public void encodePassword(PasswordEncoder passwordEncoder) {
		this.password = passwordEncoder.encode(password);
	}

	@Builder
	public User(
		String username,
		String baekjoonId,
		String githubId,
		String studentId,
		String password,
		String discordId,
		String notionEmail
	) {
		this.username = username;
		this.baekjoonId = baekjoonId;
		this.githubId = githubId;
		this.studentId = studentId;
		this.profileColor = "Blue";
		this.password = password;
		this.discordId = discordId;
		this.notionEmail = notionEmail;
		this.course = Course.FULL;
		this.authority = Authority.ROLE_GUEST;
		this.rank = 0;
		this.coin = 0;
	}
}

