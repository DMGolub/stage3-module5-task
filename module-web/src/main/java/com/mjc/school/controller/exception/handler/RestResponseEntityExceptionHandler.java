package com.mjc.school.controller.exception.handler;

import com.mjc.school.controller.exception.ApiVersionNotSupportedException;
import com.mjc.school.service.exception.EntityConstraintViolationServiceException;
import com.mjc.school.service.exception.EntityNotFoundException;
import com.mjc.school.service.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.mjc.school.controller.exception.WebErrorCode.API_VERSION_NOT_SUPPORTED;
import static com.mjc.school.controller.exception.WebErrorCode.IDS_DO_NOT_MATCH;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = {ApiVersionNotSupportedException.class})
	protected ResponseEntity<ErrorResponse> handleApiVersionNotSupportedException(
		final ApiVersionNotSupportedException e
	) {
		return buildErrorResponse(
			API_VERSION_NOT_SUPPORTED.getMessage(),
			API_VERSION_NOT_SUPPORTED.getCode(),
			e.getMessage(),
			HttpStatus.SERVICE_UNAVAILABLE
		);
	}

	@ExceptionHandler(value = {IllegalArgumentException.class})
	protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(final IllegalArgumentException e) {
		return buildErrorResponse(
			IDS_DO_NOT_MATCH.getMessage(),
			IDS_DO_NOT_MATCH.getCode(),
			e.getMessage(),
			HttpStatus.CONFLICT
		);
	}

	@ExceptionHandler(value = {ValidationException.class})
	protected ResponseEntity<ErrorResponse> handleValidationException(final ValidationException e) {
		return buildErrorResponse(
			e.getMessage(),
			e.getErrorCode(),
			e.getMessage(),
			HttpStatus.BAD_REQUEST
		);
	}

	@ExceptionHandler(value = {EntityConstraintViolationServiceException.class})
	protected ResponseEntity<ErrorResponse> handleEntityConstraintViolationServiceException(
		final EntityConstraintViolationServiceException e
	) {
		return buildErrorResponse(
			e.getMessage(),
			e.getErrorCode(),
			e.getMessage(),
			HttpStatus.CONFLICT
		);
	}

	@ExceptionHandler(value = {EntityNotFoundException.class})
	protected ResponseEntity<ErrorResponse> handleEntityNotFoundException(final EntityNotFoundException e) {
		return buildErrorResponse(
			e.getMessage(),
			e.getErrorCode(),
			e.getMessage(),
			HttpStatus.NOT_FOUND
		);
	}

	@ExceptionHandler(value = {Exception.class})
	protected ResponseEntity<ErrorResponse> handleException(final EntityNotFoundException e) {
		return buildErrorResponse(
			e.getMessage(),
			e.getErrorCode(),
			e.getMessage(),
			HttpStatus.INTERNAL_SERVER_ERROR
		);
	}

	private ResponseEntity<ErrorResponse> buildErrorResponse(
		final String message,
		final String code,
		final String errorDetails,
		final HttpStatus status
	) {
		return new ResponseEntity<>(new ErrorResponse(message, code, errorDetails), status);
	}
}