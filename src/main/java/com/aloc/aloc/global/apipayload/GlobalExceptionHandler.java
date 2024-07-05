package com.aloc.aloc.global.apipayload;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.aloc.aloc.global.apipayload.exception.ScrapException;
import com.aloc.aloc.global.apipayload.status.ErrorStatus;


@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public CustomApiResponse<String> handleIllegalArgumentException(IllegalArgumentException ex) {
		return CustomApiResponse.onFailure(
			ErrorStatus._BAD_REQUEST.getCode(),
			ex.getMessage(),
			ErrorStatus._BAD_REQUEST.getMessage()
		);
	}

	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public CustomApiResponse<String> handleIllegalStateException(IllegalStateException ex) {
		return CustomApiResponse.onFailure(
			ErrorStatus._UNAUTHORIZED.getCode(),
			ex.getMessage(),
			ErrorStatus._UNAUTHORIZED.getMessage()
		);
	}

	@ExceptionHandler(ScrapException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public CustomApiResponse<String> handleScrapException(ScrapException ex) {
		return CustomApiResponse.onFailure(
			ErrorStatus._INTERNAL_SERVER_ERROR.getCode(),
			ex.getMessage(),
			ErrorStatus._INTERNAL_SERVER_ERROR.getMessage()
		);
	}
}
