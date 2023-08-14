package com.mjc.school.service.exception;

public class EntityConstraintViolationServiceException extends ServiceException {

	public EntityConstraintViolationServiceException(final String message, final String errorCode) {
		super(message, errorCode);
	}
}