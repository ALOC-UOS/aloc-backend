package com.aloc.aloc.marathon;

import com.aloc.aloc.global.domain.AuditingTimeEntity;
import com.aloc.aloc.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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
public class Marathon extends AuditingTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@OneToOne
	private User user;

	private Integer weeklyDistance;
	private Integer totalDistance;

	@Builder
	public Marathon(
		User user
	) {
		this.user = user;
		this.weeklyDistance = 0;
		this.totalDistance = 0;
	}
}
