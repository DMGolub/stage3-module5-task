package com.mjc.school.service.exception;

public class ValidationException extends ServiceException {

	public ValidationException(final String message, final String errorCode) {
		super(message, errorCode);
	}
}