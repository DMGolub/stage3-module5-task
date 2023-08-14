package com.mjc.school.service.dto;

import java.time.LocalDateTime;

public record CommentResponseDto(
	Long id,
	String content,
	Long newsId,
	LocalDateTime createDate,
	LocalDateTime lastUpdateDate
) {
	// Empty
}
