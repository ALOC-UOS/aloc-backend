package com.aloc.aloc.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import com.aloc.aloc.common.domain.AuditingTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;



@Entity
@Getter
@Setter
@Table(name = "study_user")
public class User extends AuditingTimeEntity {
	@Id
	@Column(length = 36)
	private String id;

	@PrePersist
	public void generateUuid() {
		if (this.id == null) {
			this.id = UUID.randomUUID().toString();
		}
	}

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String baekjoonId;

	@Column(nullable = false)
	private String githubId;

	@Column(nullable = false)
	private String studentId;

	@Column(nullable = false)
	private Integer profileNumber;

	// todo: 마이그레이션 이후 삭제 예정
	@Column(nullable = false)
	private LocalDateTime joinedAt;

	private Integer rank;

	@Column(nullable = false)
	private Integer coin = 0;

	@Column(nullable = false)
	private String profileColor = "default";

	@Column(nullable = false)
	private String password;
}
