package com.mjc.school.service.dto;

import java.time.LocalDateTime;

public record AuthorResponseDto(
	Long id,
	String name,
	LocalDateTime createDate,
	LocalDateTime lastUpdateDate
) {
	// Empty
}