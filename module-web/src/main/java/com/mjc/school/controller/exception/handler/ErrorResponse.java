package com.mjc.school.controller.exception.handler;

public class ErrorResponse {

	private final String message;
	private final String code;
	private final String errorDetails;

	public ErrorResponse(final String message, final String code, final String errorDetails) {
		this.message = message;
		this.code = code;
		this.errorDetails = errorDetails;
	}

	public String getMessage() {
		return message;
	}

	public String getCode() {
		return code;
	}

	public String getErrorDetails() {
		return errorDetails;
	}
}