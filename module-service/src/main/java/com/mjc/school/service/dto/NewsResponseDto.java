package com.mjc.school.service.dto;

import java.time.LocalDateTime;
import java.util.List;

public record NewsResponseDto(
	Long id,
	String title,
	String content,
	LocalDateTime createDate,
	LocalDateTime lastUpdateDate,
	Long authorId,
	List<Long> tags,
	List<Long> comments
) {
	// Empty
}