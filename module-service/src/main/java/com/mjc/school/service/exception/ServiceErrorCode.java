package com.mjc.school.service.exception;

public enum ServiceErrorCode {
	CONSTRAINT_VIOLATION(Constants.ERROR_000001, "Validation failed: %s"),
	ENTITY_NOT_FOUND_BY_ID(Constants.ERROR_000101, "Can not find %s by id: %s"),
	AUTHOR_CONSTRAINT_VIOLATION(Constants.ERROR_001001, "Author has a persistence conflict: " +
		"name already exists"),
	TAG_CONSTRAINT_VIOLATION(Constants.ERROR_001002, "Tag has a persistence conflict: " +
		"name already exists"),
	NEWS_CONSTRAINT_VIOLATION(Constants.ERROR_001003, "News has a persistence conflict: " +
		"title already exists");

	private final String errorCode;
	private final String errorMessage;

	ServiceErrorCode(final String errorCode, final String errorMessage) {
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
		private static final String ERROR_000001 = "000001";
		private static final String ERROR_000101 = "000101";
		private static final String ERROR_001001 = "001001";
		private static final String ERROR_001002 = "001002";
		private static final String ERROR_001003 = "001003";
	}
}