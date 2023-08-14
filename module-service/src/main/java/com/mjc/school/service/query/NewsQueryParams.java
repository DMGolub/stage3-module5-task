package com.mjc.school.service.query;

import java.util.List;

public record NewsQueryParams(
	List<String> tagNames,
	List<Long> tagIds,
	String authorName,
	String title,
	String content
) {
	// Empty
}