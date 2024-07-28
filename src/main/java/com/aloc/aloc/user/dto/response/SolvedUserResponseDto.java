package com.aloc.aloc.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class SolvedUserResponseDto extends UserResponseDto {
	@Schema(description = "해결 시간", example = "2021-08-01T00:00:00")
	private String solvedAt;
	protected SolvedUserResponseDto() {
		super();
	}
}
