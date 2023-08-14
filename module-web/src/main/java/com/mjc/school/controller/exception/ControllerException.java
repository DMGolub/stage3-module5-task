package com.mjc.school.controller.exception;

public class ControllerException extends RuntimeException {

	private final String errorCode;

	public ControllerException(final String message, final String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}
}