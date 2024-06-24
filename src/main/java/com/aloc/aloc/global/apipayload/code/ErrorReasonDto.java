package com.aloc.aloc.global.apipayload.code;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorReasonDto<T> {

	private final HttpStatus httpStatus;
	private final boolean isSuccess;
	private final String code;
	private final String message;

	public boolean isSuccess() {
		return isSuccess;
	}
}
