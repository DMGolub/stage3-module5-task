package com.mjc.school.service.exception;

public final class EntityNotFoundException extends ServiceException {

	public EntityNotFoundException(final String message, final String errorCode) {
		super(message, errorCode);
	}
}