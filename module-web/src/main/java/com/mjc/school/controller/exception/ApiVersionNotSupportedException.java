package com.mjc.school.controller.exception;

public class ApiVersionNotSupportedException extends ControllerException {

	public ApiVersionNotSupportedException(final String message, final String errorCode) {
		super(message, errorCode);
	}
}