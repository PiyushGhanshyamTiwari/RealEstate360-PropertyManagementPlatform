package com.cts.exception;

public class TenantIdNotFoundException extends RuntimeException {
	public TenantIdNotFoundException(String message) {
		super(message);
	}
}
