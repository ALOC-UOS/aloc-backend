package com.aloc.aloc.user.dto.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class SolvedUserResponseDto extends UserResponseDto {
	private String solvedAt;
	protected SolvedUserResponseDto() {
		super();
	}
}
