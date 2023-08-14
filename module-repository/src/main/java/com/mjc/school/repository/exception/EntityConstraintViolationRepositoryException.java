package com.mjc.school.repository.exception;

public class EntityConstraintViolationRepositoryException extends RuntimeException {

	public EntityConstraintViolationRepositoryException(final String message) {
		super(message);
	}
}