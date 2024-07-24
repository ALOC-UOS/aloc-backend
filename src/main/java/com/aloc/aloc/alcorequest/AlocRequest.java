package com.aloc.aloc.alcorequest;

import com.aloc.aloc.global.domain.AuditingTimeEntity;
import com.aloc.aloc.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlocRequest extends AuditingTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	@Column(nullable = false)
	private String requestType;
	@Column(nullable = false)
	private Boolean isResolved = false;

	@Builder
	public AlocRequest(User user, String requestType) {
		this.user = user;
		this.requestType = requestType;
		this.isResolved = false;
	}

	public void setIsResolvedTrue() {
		this.isResolved = true;
	}
}
