package com.aloc.aloc.user;


import org.springframework.security.crypto.password.PasswordEncoder;

import com.aloc.aloc.global.domain.AuditingTimeEntity;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.user.enums.Authority;

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

	private String profileNumber;

	private Integer rank;

	private Integer coin;

	@Enumerated(EnumType.STRING)
	private Course course;

	@Column(nullable = false)
	private String profileColor;

	@Column(nullable = false)
	private String password = "password";

	@Enumerated(EnumType.STRING)
	private Authority authority;

	@Column(length = 1000)
	private String refreshToken;

	@Column
	private Integer solvedCount = 0;


	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void destroyRefreshToken() {
		this.refreshToken = null;
	}

	public void encodePassword(PasswordEncoder passwordEncoder) {
		this.password = passwordEncoder.encode(password);
	}

	public void addSolvedCount() {
		this.solvedCount++;
	}

	@Builder
	public User(
		String username,
		String baekjoonId,
		String githubId,
		String studentId,
		String password,
		String discordId,
		Integer rank,
		String notionEmail,
		String profileNumber
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
		this.profileNumber = profileNumber;
		this.rank = rank;
		this.coin = 0;
	}

	public void addCoin(int coinToAdd) {
		this.coin += coinToAdd;
	}
}

