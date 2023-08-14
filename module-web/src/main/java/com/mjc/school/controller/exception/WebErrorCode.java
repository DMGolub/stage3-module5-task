package com.mjc.school.controller.exception;

public enum WebErrorCode {

	API_VERSION_NOT_SUPPORTED(Constants.ERROR_100001, "This API version is not supported"),
	IDS_DO_NOT_MATCH(Constants.ERROR_100002, "Id mentioned in URL is not equal " +
		"to id in the request body");

	private final String errorCode;
	private final String errorMessage;

	WebErrorCode(final String errorCode, final String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getCode() {
		return errorCode;
	}

	public String getMessage() {
		return errorMessage;
	}

	private static class Constants {
		private static final String ERROR_100001 = "100001";
		private static final String ERROR_100002 = "100002";
	}
}