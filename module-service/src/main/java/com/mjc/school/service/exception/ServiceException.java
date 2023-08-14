package com.mjc.school.service.exception;

public class ServiceException extends RuntimeException {

	private final String errorCode;

	public ServiceException(final String message, final String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}
}