package com.mjc.school.controller.versioning;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

public class ApiVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

	@Override
	protected RequestCondition<ApiVersionCondition> getCustomTypeCondition(final Class<?> handlerType) {
		return createCondition(AnnotationUtils.findAnnotation(handlerType, ApiVersion.class));
	}

	@Override
	protected RequestCondition<ApiVersionCondition> getCustomMethodCondition(final Method method) {
		return createCondition(AnnotationUtils.findAnnotation(method, ApiVersion.class));
	}

	private RequestCondition<ApiVersionCondition> createCondition(final ApiVersion apiVersion) {
		return apiVersion == null ? null : new ApiVersionCondition(apiVersion.value());
	}
}