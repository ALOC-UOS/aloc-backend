package com.aloc.aloc.global.apipayload.exception;

public class AlreadyPurchasedException extends RuntimeException {
  public AlreadyPurchasedException(String message) {
    super((message));
  }
}
