package com.aloc.aloc.global.apipayload.exception;

public class ScrapException extends RuntimeException {
	public ScrapException(String message) {
		super(message);
	}

	public ScrapException(String message, Throwable cause) {
		super(message, cause);
	}
}
