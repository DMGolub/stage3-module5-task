package com.mjc.school.controller.config;

import com.mjc.school.controller.versioning.ApiVersionRequestMappingHandlerMapping;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class WebMvcRegistrationsConfig implements WebMvcRegistrations {

	@Override
	public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
		final ApiVersionRequestMappingHandlerMapping handlerMapping =
			new ApiVersionRequestMappingHandlerMapping();
		handlerMapping.setOrder(0);
		handlerMapping.setRemoveSemicolonContent(false);
		return handlerMapping;
	}
}