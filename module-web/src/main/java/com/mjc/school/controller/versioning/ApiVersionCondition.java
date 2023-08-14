package com.mjc.school.controller.versioning;

import com.mjc.school.controller.exception.ApiVersionNotSupportedException;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mjc.school.controller.exception.WebErrorCode.API_VERSION_NOT_SUPPORTED;

public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {

	private static final Pattern VERSION_PREFIX_PATTERN = Pattern.compile("/v(\\d+)/");

	private final int apiVersion;

	public ApiVersionCondition(final int apiVersion) {
		this.apiVersion = apiVersion;
	}

	@Override
	public ApiVersionCondition combine(final ApiVersionCondition other) {
		return new ApiVersionCondition(other.getApiVersion());
	}

	@Override
	public ApiVersionCondition getMatchingCondition(final HttpServletRequest request) {
		final Matcher matcher = VERSION_PREFIX_PATTERN.matcher(request.getRequestURI());
		if (matcher.find()) {
			final int version = Integer.parseInt(matcher.group(1));
			if (version == this.apiVersion) {
				return this;
			} else {
				throw new ApiVersionNotSupportedException(
					String.format("Api version '%s' is not supported.", version),
					API_VERSION_NOT_SUPPORTED.getCode());
			}
		}
		throw new ApiVersionNotSupportedException(
			String.format("Api version in the request uri '%s' is not supported.", request.getRequestURI()),
			API_VERSION_NOT_SUPPORTED.getCode()
		);
	}

	@Override
	public int compareTo(final ApiVersionCondition other, final HttpServletRequest request) {
		return other.getApiVersion() - this.getApiVersion();
	}

	private int getApiVersion() {
		return apiVersion;
	}
}