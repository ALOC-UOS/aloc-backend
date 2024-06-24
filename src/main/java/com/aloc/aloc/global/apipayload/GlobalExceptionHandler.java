package com.aloc.aloc.global.apipayload;

import com.aloc.aloc.global.apipayload.status.ErrorStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiResponse<String> handleIllegalArgumentException(IllegalArgumentException ex) {
		return ApiResponse.onFailure(ErrorStatus._BAD_REQUEST.getCode(), ex.getMessage(), ErrorStatus._BAD_REQUEST.getMessage());
	}

	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ApiResponse<String> handleIllegalStateException(IllegalStateException ex) {
		return ApiResponse.onFailure(ErrorStatus._UNAUTHORIZED.getCode(), ex.getMessage(), ErrorStatus._UNAUTHORIZED.getMessage());
	}

//	@ExceptionHandler(Exception.class) // 예외가 처리되지 않은 다른 모든 예외에 대한 핸들러 추가
//	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//	public ApiResponse<String> handleException(Exception ex) {
//		// 예외 처리 로직 추가
//		return ApiResponse.onFailure(ErrorStatus._INTERNAL_SERVER_ERROR.getCode(), ex.getMessage(), ErrorStatus._INTERNAL_SERVER_ERROR.getMessage());
//	}
}
