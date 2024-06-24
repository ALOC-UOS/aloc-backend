package com.aloc.aloc.global.apipayload.code;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReasonDto {
	private HttpStatus httpStatus;

	private final Boolean isSuccess;
	private final String code;
	private final String message;

	public Boolean getIsSuccess() {
		return isSuccess;
	}
}
